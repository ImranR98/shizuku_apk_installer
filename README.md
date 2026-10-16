# shizuku_apk_installer

Flutter plugin for installing Android APKs using Dhizuku/Shizuku API.
With this plugin you can install and uninstall apps (APKs and AABs) in background without asking user.
Also you can pretend to be another package (like Google Play) when installing (only Shizuku).

## Installer mode

By default the plugin uses Shizuku/Sui. To use Dhizuku exclusively, prefer Shizuku with a Dhizuku fallback, or offer separate options in your app, call [setInstallerMode] before [checkPermission] or install calls:

```dart
import 'package:shizuku_apk_installer/shizuku_apk_installer.dart';

final installer = ShizukuApkInstaller();

await installer.setInstallerMode(InstallerMode.auto);
// or InstallerMode.dhizuku, or InstallerMode.shizuku

final permission = await installer.checkPermission();
```

[InstallerMode.auto] prefers Shizuku/Sui and falls back to Dhizuku when Shizuku is unavailable; [InstallerMode.shizuku] and [InstallerMode.dhizuku] never use the other backend.

**Backward compatibility:** versions before 1.3.0 attempted Dhizuku first automatically. If your app relied on that behavior, call `setInstallerMode(InstallerMode.dhizuku)` before permission and install calls.

[setInstallerMode]: lib/shizuku_apk_installer.dart
[checkPermission]: lib/shizuku_apk_installer.dart
