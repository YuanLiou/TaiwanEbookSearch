package com.rayliu.commonmain.di

import com.rayliu.commonmain.LevenshteinDistanceHelper
import com.rayliu.commonmain.LevenshteinDistanceHelperImpl
import com.rayliu.commonmain.OffsetDateTimeHelper
import org.koin.dsl.module

val miscModule =
    module {
        factory<LevenshteinDistanceHelper> {
            LevenshteinDistanceHelperImpl()
        }
        factory { OffsetDateTimeHelper() }
    }
