package liou.rayyuan.ebooksearchtaiwan.model

import android.os.Build

object SystemInfoCollector {
    fun getUserAgent(): String {
        return "Android/${getAndroidVersion()} Device/${getAndroidDeviceName()}"
    }

    private fun getAndroidVersion(): Int = Build.VERSION.SDK_INT

    private fun getAndroidDeviceName(): String = Build.MODEL
}