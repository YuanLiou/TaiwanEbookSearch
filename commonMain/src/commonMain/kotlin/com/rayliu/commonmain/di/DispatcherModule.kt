package com.rayliu.commonmain.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dispatcherModule =
    module {
        factory<CoroutineDispatcher>(named("IO")) {
            Dispatchers.IO
        }
        factory<CoroutineDispatcher>(named("Default")) {
            Dispatchers.Default
        }
    }
