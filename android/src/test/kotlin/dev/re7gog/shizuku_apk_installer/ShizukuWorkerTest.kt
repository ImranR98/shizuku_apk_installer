package dev.re7gog.shizuku_apk_installer

import android.content.pm.IPackageInstaller
import android.content.pm.PackageInstaller
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.lsposed.hiddenapibypass.HiddenApiBypass
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow
import org.robolectric.util.ReflectionHelpers
import org.robolectric.util.ReflectionHelpers.ClassParameter
import java.lang.reflect.Proxy

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [30, 31],
    manifest = Config.NONE,
    shadows = [HiddenApiBypassShadow::class],
    instrumentedPackages = ["org.lsposed.hiddenapibypass"]
)
class ShizukuWorkerTest {
    @Test
    fun successiveSessionsUseTheirOwnSource() {
        val sources = mutableListOf<String>()
        val installer = Proxy.newProxyInstance(
            IPackageInstaller::class.java.classLoader,
            arrayOf(IPackageInstaller::class.java)
        ) { _, method, args ->
            when (method.name) {
                "createSession" -> {
                    sources += args!![1] as String
                    sources.size
                }
                else -> null
            }
        } as IPackageInstaller
        val context = RuntimeEnvironment.getApplication()
        val worker = ShizukuWorker(context)
        ReflectionHelpers.setField(worker, "iPackageInstaller\$delegate", lazyOf(installer))
        ReflectionHelpers.setField(worker, "contextS\$delegate", lazyOf(context))
        val create = ShizukuWorker::class.java.getDeclaredMethod(
            "createPackageInstaller", String::class.java
        ).apply { isAccessible = true }
        val requested = listOf(
            "", "com.android.vending", "com.sec.android.app.samsungapps",
            "", "com.sec.android.app.samsungapps", "com.android.vending"
        )
        requested.forEach { source ->
            val packageInstaller = create.invoke(worker, source) as PackageInstaller
            Shadow.directlyOn<Int, PackageInstaller>(
                packageInstaller, PackageInstaller::class.java, "createSession",
                ClassParameter.from(
                    PackageInstaller.SessionParams::class.java,
                    PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
                )
            )
        }
        assertEquals(
            requested.map { it.ifEmpty { "com.android.shell" } }, sources
        )
    }
}

@Implements(value = HiddenApiBypass::class, isInAndroidSdk = false)
class HiddenApiBypassShadow {
    companion object {
        @JvmStatic
        @Implementation
        fun __staticInitializer__() = Unit

        @JvmStatic
        @Implementation
        fun addHiddenApiExemptions(vararg signaturePrefixes: String): Boolean = true
    }
}
