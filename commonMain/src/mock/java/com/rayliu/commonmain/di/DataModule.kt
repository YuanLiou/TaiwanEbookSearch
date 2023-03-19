package com.rayliu.commonmain.di

import com.rayliu.commonmain.data.BookSearchApi
import com.rayliu.commonmain.data.api.BookSearchService
import org.koin.dsl.module

val dataModule = module {

    // Provide: BookSearchService
    factory<BookSearchService> {
        BookSearchApi()
    }
}
