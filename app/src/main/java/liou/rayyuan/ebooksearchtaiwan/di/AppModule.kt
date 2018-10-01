package liou.rayyuan.ebooksearchtaiwan.di

import liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchPresenter
import liou.rayyuan.ebooksearchtaiwan.model.APIManager
import liou.rayyuan.ebooksearchtaiwan.model.EventTracker
import liou.rayyuan.ebooksearchtaiwan.model.RemoteConfigManager
import liou.rayyuan.ebooksearchtaiwan.model.UserPreferenceManager
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module

/**
 * Created by louis383 on 2018/8/29.
 */

val appModule = module {

    single { APIManager() }
    single { RemoteConfigManager() }
    factory { EventTracker(androidApplication()) }
    factory { UserPreferenceManager(androidApplication()) }

    // Presenters
    factory { BookSearchPresenter(get(), get()) }

}

val appModules = listOf(appModule)
