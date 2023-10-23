package com.rayliu.commonmain.di

import org.apache.commons.text.similarity.LevenshteinDistance
import org.koin.dsl.module

private const val SIMILARITY_THRESHOLD = 7

val miscModule = module {
    single<LevenshteinDistance> {
        LevenshteinDistance(SIMILARITY_THRESHOLD)
    }
}
