package com.rayliu.commonmain

interface SystemInfoCollector {
    fun getUserAgent(): String

    fun getApplicationVersionName(): String
}
