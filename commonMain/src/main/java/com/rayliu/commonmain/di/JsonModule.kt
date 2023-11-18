package com.rayliu.commonmain.di

import kotlinx.serialization.json.Json
import org.koin.dsl.module

val jsonModule = module {
    single<Json> {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }
    }
}
