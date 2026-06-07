# Android TV app + modularization plan

Modules already created: `:core` (android-library), `:mobile` (the existing app,
renamed from `:app`), `:tv` (Compose-for-TV app scaffold).

Goal: a TV (10-foot, D-pad) version of the installer that **reuses the install/
manage engine** via `:core`, without breaking the shipping `:mobile` app.

## Target architecture

```
:core   ← UI-agnostic engine: domain models + data/engine (no Compose, no Koin)
:mobile ← existing phone UI  (keeps working throughout; migrates onto :core later)
:tv     ← new 10-foot UI (tv-material, D-pad) on top of :core
```

`:mobile` is NOT migrated in early phases — that's a large, risky rewrite of 149
files. We grow `:core` incrementally and let `:tv` consume it now; `:mobile`
adopts `:core` piecemeal once the API has proven out on TV.

## Phases

### Phase 1 — runnable TV MVP: manage + uninstall  ← THIS PASS
- `:core`: `domain.InstalledApp` + `AppRepository.getInstalledApps()` (PackageManager
  query: label, system flag, size, enabled, installer — permission-free, no UsageStats).
- `:tv`: depend on `:core`; replace the template Greeting with a D-pad `ManageScreen`
  (TvLazyColumn of focusable app cards) + uninstall via the system `ACTION_DELETE`
  dialog (no privileged perm needed). Manifest: QUERY_ALL_PACKAGES + leanback.
- Acceptance: launches on a TV/emulator, lists user apps, focus moves with D-pad,
  selecting an app → system uninstall flow → list refreshes.

### Phase 2 — install from storage (TV)
- `:core`: `ApkInstaller` (PackageInstaller session, single + split bundle) with a
  result Flow; `ApkScanner` to find APK/APKS in Download + `/storage/*` (USB).
- `:tv`: an "Install" destination — browse found packages, confirm, install with
  progress. Storage permission flow (MANAGE_EXTERNAL_STORAGE / scoped) for TV.

### Phase 3 — network push + downloader on TV
- Reuse `:mobile`'s sync HTTP-server idea in `:core` so a phone can push an APK to
  the TV (text entry on TV is painful — this sidesteps it). Optional URL download.

### Phase 4 — privileged backends on TV
- Move Shizuku / Root (libsu) install paths into `:core`; expose in `:tv`. Many TV
  boxes are rooted, so this is high-value there.

### Phase 5 — migrate `:mobile` onto `:core`
- Replace `:mobile`'s duplicated engine pieces with `:core` calls, module by module
  (models → repository → installers), keeping the app green at each step. End state:
  one engine, two UIs.

## Notes / constraints
- TV input: no file-manager VIEW/SEND intents like phone — entry is USB browse,
  network push, or downloader (Phases 2–3).
- Text entry on TV is clunky → minimize text fields.
- `:core` stays Compose-free and Koin-free so both UIs (and tests) can use it plainly.
