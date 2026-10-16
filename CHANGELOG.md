## 1.4.0

* Add `InstallerMode.auto`: prefers Shizuku/Sui and falls back to Dhizuku when Shizuku is unavailable
* Route permission checks, installs and uninstalls through a single backend preparation step

## 1.3.0

* Add `InstallerMode` enum and `setInstallerMode()` to choose Dhizuku or Shizuku/Sui explicitly
* Lazy-init installer backends based on selected mode instead of always preferring Dhizuku
* **Behavior change:** default mode is Shizuku when `setInstallerMode` is not called (previously Dhizuku was tried first automatically)

## 1.2.0

* Updated dependencies; Android 8.0 minimum required
