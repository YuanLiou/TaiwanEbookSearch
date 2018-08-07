package liou.rayyuan.ebooksearchtaiwan.model

import android.os.Build
import liou.rayyuan.ebooksearchtaiwan.BuildConfig

object SystemInfoCollector {
    fun getUserAgent(): String {
        return "Android/${getAndroidVersion()} Device/${getAndroidDeviceName()} AppVersion/${getApplicationVersionName()}"
    }

    private fun getAndroidVersion(): Int = Build.VERSION.SDK_INT

    private fun getAndroidDeviceName(): String = Build.MODEL

    private fun getApplicationVersionName(): String = BuildConfig.VERSION_NAME
}