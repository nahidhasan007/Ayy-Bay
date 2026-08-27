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
| Signing | Not configured — no release keystore yet |

Version name/build number are now visible in-app under **Profile** (tap the avatar on Home → Profile), alongside the signed-in account's name and email. Useful for confirming exactly which build a bug report came from.

## Phase 0 — Close the blockers

Nothing below this line ships to a real user until every item here is done. See the in-app testing session notes for how each was found.

- [ ] **Replace the launcher icon.** `ic_launcher.webp` / `ic_launcher_round.webp` are still Android Studio's default template icon. Needs real adaptive-icon layers plus a 512×512 hi-res icon for the store listing.
- [ ] **Configure Google Sign-In.** `google_web_client_id` in `strings.xml` is still the placeholder `REPLACE_WITH_YOUR_WEB_CLIENT_ID...` — sign-in does not work. Create a real OAuth Web Client ID in Google Cloud Console and register the SHA-1 fingerprint for both the debug keystore and the release keystore (once it exists) against it.
- [ ] **Create a release signing config.** Generate an upload keystore (Android Studio → *Generate Signed Bundle / APK*), store it outside git, and either wire a `signingConfigs.release` block into `app/build.gradle.kts` or sign manually at release time.
- [ ] **Write and host a Privacy Policy.** Required by Play Console for every listing, and mandatory here since the app collects a name and email via Google Sign-In. Needs to be reachable at a public URL before the Play Console listing can go live.
- [ ] **Fill in the Data Safety form** in Play Console once the privacy policy exists — declare account email (via Google Sign-In) and locally-stored financial/notes data, no third-party sharing.
- [ ] **Resolve the unused location permissions.** `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` are declared in the manifest but nothing in the codebase calls a location API — prayer times use a hardcoded Dhaka coordinate. Either remove the permissions or implement real location-based prayer time detection.
- [ ] **Produce store listing assets.** Screenshots (phone, and tablet if supporting it), a 1024×500 feature graphic, the 512×512 hi-res icon, an 80-character short description, and a full description.
- [ ] **Add an account/data deletion path.** Play policy requires apps with account creation to offer account and data deletion, in-app or via a linked web form.

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

- Who owns the Google Cloud Console project the OAuth client will live in, and is billing/verification already set up on it?
- Is there an existing brand mark for "Jibon" to base the launcher icon on, or does one need to be designed from scratch?
- Where will the Privacy Policy be hosted (a simple static page is enough to start — GitHub Pages or a single hosted HTML file both work)?
