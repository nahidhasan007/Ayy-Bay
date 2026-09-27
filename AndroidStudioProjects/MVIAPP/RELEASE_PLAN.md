# Release Plan

Status: pre-launch. This document tracks what stands between the current build and a public Play Store release, and the phased rollout once it's ready.

## Current build

| | |
|---|---|
| Package | `com.ayybay.app` |
| Version name | `1.0` |
| Version code | `1` |
| Flavors | `prod`, `stage` (`stage` appends `.stage` to the applicationId and `-stage` to the version name) |
| Min / Target SDK | 24 / 36 |
| Signing | `signingConfigs.release` wired in `app/build.gradle.kts`, sourced from a git-ignored `keystore.properties` (see `keystore.properties.example`) — but no real upload keystore has been generated yet, so release builds are still unsigned until one exists |

Version name/build number are now visible in-app under **Profile** (tap the avatar on Home → Profile), alongside the signed-in account's name and email. Useful for confirming exactly which build a bug report came from.

## Phase 0 — Close the blockers

Nothing below this line ships to a real user until every item here is done. See the in-app testing session notes for how each was found.

- [x] **Replace the launcher icon.** Done — the real "Jibon" mark now drives a proper adaptive icon (`ic_launcher_foreground.png` inset in a safe zone + solid brand-green `ic_launcher_background.xml`), regenerated legacy per-density mipmaps (mdpi–xxxhdpi, square + round), and the manifest points back at `@mipmap/ic_launcher`/`@mipmap/ic_launcher_round`. A 512×512 hi-res copy for the store listing is at `store-assets/icon-512.png`.
- [ ] **Configure Google Sign-In.** `google_web_client_id` in `strings.xml` is still the placeholder `REPLACE_WITH_YOUR_WEB_CLIENT_ID...` — sign-in does not work. Create a real OAuth Web Client ID in Google Cloud Console and register the SHA-1 fingerprint for both the debug keystore and the release keystore (once it exists) against it. **This blocks real (non-guest) sign-in end to end — nothing else in code can substitute for this manual console step.**
- [ ] **Generate the release upload keystore.** `app/build.gradle.kts` now has a `signingConfigs.release` block that reads `keystore.properties` (git-ignored, see `keystore.properties.example`) when present, and release builds get `isMinifyEnabled`/`isShrinkResources` on with app-specific ProGuard keep rules for Room entities and the Google Identity/Credential Manager classes. **Decision: generating this via Android Studio (Build → Generate Signed Bundle/APK) rather than the CLI**, so passwords are set interactively rather than passed through chat. Once generated, copy `keystore.properties.example` → `keystore.properties` and fill in the real `storeFile`/passwords — store the `.jks` and its passwords somewhere safe outside git (a password manager), losing them means you can never publish an update to the same listing again. Independently re-verified both `:app:assembleProdDebug` and `:app:assembleProdRelease` (R8 minified) build green as of September 27, 2026; still needs a real-device smoke test of a signed release build before shipping (R8 issues can be runtime-only).
- [ ] **Write a Privacy Policy.** Done in content — `store-assets/privacy-policy.html` (identical copy at `docs/index.html`) and an in-app copy at Profile → Privacy Policy / the sign-in screens, dated September 27, 2026, with the support contact set to `nahidhasan99679@gmail.com`. **Still needs**: push this branch/repo to `origin` and enable Settings → Pages → Deploy from branch `main` → `/docs`, then put the resulting `https://<org>.github.io/<repo>/` URL into Play Console's Privacy Policy field. Not done yet — no push has happened.
- [ ] **Fill in the Data Safety form** in Play Console once the privacy policy is hosted — declare account email/name/photo (via Google Sign-In, not shared with third parties), on-device financial/notes/Quran-progress data, location (used, not shared), contacts (used, not shared), and that all of it is user-deletable in-app.
- [x] **Location is real, not hardcoded.** `LocationProvider` (FusedLocationProviderClient + Geocoder) now backs prayer-time/Qibla calculation via `PrayerViewModel`, with a Dhaka fallback when permission is denied — the permissions are used, not dead weight.
- [ ] **Produce store listing assets.** Screenshots (phone, and tablet if supporting it), a 1024×500 feature graphic, an 80-character short description, and a full description. The 512×512 hi-res icon is done (`store-assets/icon-512.png`).
- [x] **Add an account/data deletion path.** Done — Profile → "Delete account & data" wipes every local Room table plus the cached Google session (there is no server-side account, so this is the complete data-deletion story) after a confirmation dialog, then signs the user out.
- [x] **Fix the in-app WebView's cleartext HTTP block.** Two seeded Daily Links entries (`educationboardresults.gov.bd`, `hospitaldghs.gov.bd`) are HTTP-only government sites; targeting SDK 28+ blocks cleartext by default, so `LinkWebViewScreen` would have shown a blank/error page for these two links. Added `app/src/main/res/xml/network_security_config.xml` allowlisting just those two domains for cleartext, everything else still requires HTTPS.

### Play Console Data Safety form — what to declare

Map straight from what the code actually does (see `store-assets/privacy-policy.html` for the user-facing version):

| Data type | Collected? | Shared with 3rd parties? | Purpose | User can request deletion? |
|---|---|---|---|---|
| Name, email, profile photo | Yes, via Google Sign-In only if the user signs in (not guest mode) | No | Account management / app functionality | Yes, in-app |
| Approximate/precise location | Yes, with runtime permission | No | App functionality (prayer times, Qibla) | N/A — not stored server-side |
| Contacts | Yes, read on-demand for the Phone Book feature | No | App functionality | N/A — not stored, just displayed |
| Financial info, app activity (transactions, notes, prayer logs, Quran progress, job bookmarks) | Yes, stored locally only | No | App functionality | Yes, in-app ("Delete account & data") |

No ads SDK, no analytics SDK, no crash reporting SDK are integrated as of this writing — if Crashlytics is added later (Phase 2 recommends it), this table and the privacy policy both need updating before the next submission.

## Phase 1 — Internal testing

Once Phase 0 is complete:

1. Bump `versionCode` to `2`, keep `versionName` at `1.0` (or `1.0.0` if adopting semantic versioning — see below).
2. Build a signed release AAB with the real keystore and upload to the **Internal testing** track in Play Console.
3. Install on at least one physical device per major Android version in range (Android 7 through the latest), not just the emulator — pay particular attention to the exact-alarm permission flow and Adhan playback, both of which behave differently across OEM skins.
4. Smoke-test every screen after **heavy repeated use** — open and close the app a dozen times, toggle prayer settings repeatedly — not just a single fresh install. The Salah Tracker crash fixed this session only appeared after several cold starts had let data accumulate; a single fresh-install pass would have missed it.

## Phase 2 — Closed testing

- Invite a small group of real users (10–30) representative of the target audience.
- Turn on **Firebase Crashlytics** before this phase — the fastest way to catch a rare issue like the one fixed this session is real users on real devices, not another manual pass.
- Collect feedback for at least a week before moving on. Watch for: Adhan reliability across device manufacturers (battery optimization killing alarms is the most common failure mode), and any data loss on app reinstall or device change (there is currently no cloud backup — see the growth plan for why this matters for retention).

## Phase 3 — Production rollout

- Start at a **staged rollout percentage** (e.g. 10-20%) rather than 100%, and hold for a few days watching the crash rate and ANR rate in Play Console before increasing it.
- Publish the Bangla (`bn-BD`) store listing alongside English — the app itself defaults to Bangla, and an English-only listing under-converts for this audience.
- Have a rollback plan ready: if the staged rollout shows a spike in crashes, halt the rollout in Play Console before it reaches 100% rather than publishing a hotfix under pressure.

## Versioning going forward

Adopt semantic versioning (`MAJOR.MINOR.PATCH`) for `versionName` starting with the first public release, and increment `versionCode` by exactly 1 on every build submitted to Play Console, regardless of track:

- **PATCH** (`1.0.1`) — bug fixes only, no new user-facing behavior. The two fixes from this session (prayer time duplication, tile height) would have shipped as one patch release.
- **MINOR** (`1.1.0`) — new features, backward compatible (e.g. the Profile section added this session).
- **MAJOR** (`2.0.0`) — reserved for the subscription launch, since it changes the app's core value proposition and data model (cloud sync).

Every release should also get a short entry in a `CHANGELOG.md` at the repo root — not started yet, but worth beginning with whatever version first reaches Internal testing, so the Profile screen's build number always has a corresponding line of "what changed" to look up.

## Open questions to resolve before Phase 0 is complete

- Who owns the Google Cloud Console project the OAuth client will live in, and is billing/verification already set up on it? (Still the single true code blocker — `google_web_client_id` in `strings.xml` is a placeholder.)
- ~~Is there an existing brand mark for "Jibon"...~~ Resolved — `ic_jibon.webp` is the brand mark; a proper adaptive icon now derives from it.
- ~~Where will the Privacy Policy be hosted...~~ Resolved — GitHub Pages from this repo's `/docs` folder. Still needs the actual push + Pages toggle in repo settings.
