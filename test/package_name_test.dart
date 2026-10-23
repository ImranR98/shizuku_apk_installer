import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:shizuku_apk_installer/shizuku_apk_installer.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();
  final calls = <MethodCall>[];
  const channel = MethodChannel('shizuku_apk_installer');

  setUp(() {
    calls.clear();
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (call) async {
          calls.add(call);
          return 0;
        });
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
  });

  test('single APK identifies target separately from installer identity', () async {
    expect(await ShizukuApkInstaller().installAPK(
      'file:///test/base.apk', 'com.android.vending',
      packageName: 'example.internal',
    ), 0);
    expect(calls.single.method, 'installAPKs');
    expect(calls.single.arguments, {
      'apkFilesURIs': ['file:///test/base.apk'],
      'fakeInstallSource': 'com.android.vending',
      'packageName': 'example.internal',
    });
  });

  test('split APKs share one target and subsequent calls do not reuse it', () async {
    final installer = ShizukuApkInstaller();
    await installer.installAABSplits(
      ['file:///test/base.apk', 'file:///test/config.apk'], '',
      packageName: 'example.external',
    );
    await installer.installAPK('file:///test/other.apk', '',
      packageName: 'example.other');
    await installer.installAPK('file:///test/legacy.apk', '');
    expect(calls[0].arguments['packageName'], 'example.external');
    expect(calls[0].arguments['apkFilesURIs'], hasLength(2));
    expect(calls[1].arguments['packageName'], 'example.other');
    expect(calls[2].arguments.containsKey('packageName'), isFalse);
  });
}
