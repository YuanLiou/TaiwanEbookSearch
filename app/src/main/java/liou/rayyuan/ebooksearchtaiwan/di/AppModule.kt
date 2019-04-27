package liou.rayyuan.ebooksearchtaiwan.di

import androidx.room.Room
import liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchViewModel
import liou.rayyuan.ebooksearchtaiwan.model.*
import liou.rayyuan.ebooksearchtaiwan.utils.QuickChecker
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

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

    // Database related and Daos
    single {
        Room.databaseBuilder(androidApplication(),
                DatabaseManager::class.java,
                DatabaseManager.DATABASE_NAME)
                .build()
    }

    single {
        get<DatabaseManager>().searchRecordDao()
    }

    // ViewModels
    viewModel { BookSearchViewModel(get(), get(), get(), get()) }

}

val appModules = listOf(appModule)
