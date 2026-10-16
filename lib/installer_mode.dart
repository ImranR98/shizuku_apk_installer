/// Privileged installer backend selected by the host app.
enum InstallerMode {
  /// Prefer Shizuku/Sui, falling back to Dhizuku when Shizuku is unavailable.
  auto,
  dhizuku,
  shizuku,
}
