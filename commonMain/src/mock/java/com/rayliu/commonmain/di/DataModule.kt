package com.rayliu.commonmain.di

import com.rayliu.commonmain.data.BookSearchApi
import com.rayliu.commonmain.data.BookStoresApi
import com.rayliu.commonmain.data.api.BookSearchService
import com.rayliu.commonmain.data.api.BookStoresService
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dataModule =
    module {

        // Provide: BookSearchService
        factory<BookSearchService> {
            BookSearchApi(
                androidApplication().assets,
                get<Json>()
            )
        }

        // Provide: BookStoresService
        factory<BookStoresService> {
            BookStoresApi(
                androidApplication().assets,
                get<Json>()
            )
        }
    }
