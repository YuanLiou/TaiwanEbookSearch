package liou.rayyuan.ebooksearchtaiwan.di

import liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchPresenter
import liou.rayyuan.ebooksearchtaiwan.model.APIManager
import liou.rayyuan.ebooksearchtaiwan.model.EventTracker
import liou.rayyuan.ebooksearchtaiwan.model.RemoteConfigManager
import liou.rayyuan.ebooksearchtaiwan.model.UserPreferenceManager
import liou.rayyuan.ebooksearchtaiwan.utils.QuickChecker
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module

/**
 * Created by louis383 on 2018/8/29.
 */

val appModule = module {

    // use single to prevent HashSet searching slow issue
    // related to: https://github.com/InsertKoinIO/koin/issues/281
    // it will be fix in Koin 2.0
    single { APIManager() }
    single { RemoteConfigManager() }
    single { EventTracker(androidApplication()) }
    single { UserPreferenceManager(androidApplication()) }
    single { QuickChecker(androidApplication()) }

    // Presenters
    factory { BookSearchPresenter(get(), get(), get()) }

}

val appModules = listOf(appModule)
