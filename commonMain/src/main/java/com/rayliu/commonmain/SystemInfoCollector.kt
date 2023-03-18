package com.rayliu.commonmain

import android.os.Build

@Deprecated("Use a multi-platform friendly method")
object SystemInfoCollector {
    fun getUserAgent(): String {
        return "Android/${getAndroidVersion()} Device/${getAndroidDeviceName()} AppVersion/${getApplicationVersionName()}"
    }

    private fun getAndroidVersion(): Int = Build.VERSION.SDK_INT

    private fun getAndroidDeviceName(): String = Build.MODEL

    private fun getApplicationVersionName(): String = "2.1.0"
}