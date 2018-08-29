package liou.rayyuan.ebooksearchtaiwan.di

import liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchPresenter
import liou.rayyuan.ebooksearchtaiwan.model.APIManager
import liou.rayyuan.ebooksearchtaiwan.model.EventTracker
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext

/**
 * Created by louis383 on 2018/8/29.
 */

val appModule = applicationContext {

    bean { APIManager() }
    factory { EventTracker(androidApplication()) }

    // Presenters
    factory { BookSearchPresenter(get(), get()) }

}

val appModules = listOf(appModule)
