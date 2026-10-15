# Changelog

All notable changes to Jibon, tracked against the `versionCode` shown in Profile.

## Unreleased (pre-launch, versionCode 1 / 1.0.0)

- Fixed release build failure: `AndroidManifest.xml` referenced a non-existent
  `@mipmap/app_icon`; it now points back at the adaptive `@mipmap/ic_launcher`.

- Replaced the launcher icon with a proper Android adaptive icon built from the real
  "Jibon" brand mark (safe-zone-inset foreground + solid brand-green background),
  regenerated legacy per-density mipmaps, and added a 512×512 hi-res icon for the Play
  Store listing.
- Added an in-app account & data deletion path (Profile → "Delete account & data"),
  required by Play Store policy for apps with account creation.
- Added a Privacy Policy: an in-app screen (Profile, and linked from the Login/Sign up
  screens) plus a hostable `store-assets/privacy-policy.html`.
- Wired a release signing config (`app/build.gradle.kts` reads `keystore.properties` when
  present) and turned on code shrinking (`isMinifyEnabled`/`isShrinkResources`) for release
  builds, with app-specific ProGuard keep rules.
- Fixed misleading "sync" copy on the Login screen — the app has no cloud backend; all data
  is stored on-device only.
- Filled in the Privacy Policy's remaining placeholders (last-updated date, support email)
  and generated the missing 512×512 store-listing icon (`store-assets/icon-512.png`).
