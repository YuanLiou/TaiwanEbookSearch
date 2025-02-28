package liou.rayyuan.ebooksearchtaiwan.utils

import android.os.Build
import com.rayliu.commonmain.SystemInfoCollector
import liou.rayyuan.ebooksearchtaiwan.BuildConfig

class SystemInfoCollectorImpl : SystemInfoCollector {
    override fun getUserAgent(): String =
        "Android/${getAndroidVersion()} Device/${getAndroidDeviceName()} AppVersion/${getApplicationVersionName()}"

    override fun getApplicationVersionName(): String = BuildConfig.VERSION_NAME

    private fun getAndroidVersion(): Int = Build.VERSION.SDK_INT

    private fun getAndroidDeviceName(): String = Build.MODEL
}
