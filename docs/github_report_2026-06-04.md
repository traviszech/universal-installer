# Daily GitHub Issue Report - 2026-06-04

## Summary
- **Total Issues Updated:** 6
- **Open Issues:** 6
- **Priority Fixes:**
    1. #36: Semi-transparent/bouncy context menu (UI feedback)
    2. #40: Work profile installation not working
    3. #27: Option to target specific profiles for install

## Detailed Analysis

### Issue #40: Installation in the work profile does not work
- **Status:** Open
- **Summary of Comments:** No comments yet. Reporter LorianL98 states that even when selecting a work profile, the app installs in the main profile.
- **Action Taken:** Reported.
- **Suggested Next Steps:** Investigate `SessionParams` and how the user ID is being passed during the installation process.

### Issue #39: Hide-able toggle for Local/Download tabs
- **Status:** Open
- **Summary of Comments:** No comments. Request for a setting to hide the tab selector.
- **Action Taken:** Reported.
- **Suggested Next Steps:** Add a boolean toggle in Settings and update the main screen UI to hide the pager/tabs if disabled.

### Issue #38: Romanian translation
- **Status:** Open
- **Summary of Comments:** minhnq1-apero approved the contribution.
- **Action Taken:** Approved.
- **Suggested Next Steps:** Wait for PR or provide the user with the translation CSV.

### Issue #37: Split APK with 2 "ARM64-V8A" APKs refuses to download without both selected
- **Status:** Open
- **Summary of Comments:** chloricacid realized they missed diagnostics.
- **Action Taken:** Reported.
- **Suggested Next Steps:** Improve the auto-selection logic to handle multiple APKs with the same architecture tag.

### Issue #36: Semi-transparent context menu, 1.8.0
- **Status:** Open
- **Summary of Comments:** Users find the new transparent/bouncy popup distracting. `nqmgaming` confirmed they will fix it.
- **Action Taken:** Fix planned.
- **Suggested Next Steps:** Revert popup transparency/animation or provide a classic theme option.

### Issue #27: Option in Advanced tab to target a specific profile for Install
- **Status:** Open
- **Summary of Comments:** manualtrial0011 confirms selecting Work Profile (10) doesn't work, app goes to Owner (0).
- **Action Taken:** Reported (Related to #40).
- **Suggested Next Steps:** Fix the logic that maps selected profile to the installer session.

---

## Suggested Fixes (Automated Analysis)

### #36: Fix Context Menu UI
The transparency and bouncy animation in the new popup seem to be the main complaint.
```kotlin
// Investigate the implementation of the popup/context menu and adjust alpha/animations.
```

### #40/#27: Fix Work Profile Installation
The installer likely ignores the selected profile ID and defaults to the current user.
```kotlin
// Check PackageInstaller.SessionParams.setInstallerPackageName and how user handles are used.
```

## Validation & Status
- [ ] Reproduce locally (#36, #40)
- [ ] Implement Fix
- [ ] Run Build/Tests
- [ ] Create Pull Request
