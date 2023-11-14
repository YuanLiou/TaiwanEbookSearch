package com.rayliu.commonmain.di

import com.rayliu.commonmain.LevenshteinDistanceHelper
import com.rayliu.commonmain.LevenshteinDistanceHelperImpl
import org.koin.dsl.module

val miscModule = module {
    factory<LevenshteinDistanceHelper> {
        LevenshteinDistanceHelperImpl()
    }
}
