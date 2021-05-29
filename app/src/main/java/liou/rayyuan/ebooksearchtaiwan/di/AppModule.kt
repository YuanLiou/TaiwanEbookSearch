package liou.rayyuan.ebooksearchtaiwan.di

import androidx.room.Room
import liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchViewModel
import liou.rayyuan.ebooksearchtaiwan.model.*
import liou.rayyuan.ebooksearchtaiwan.utils.QuickChecker
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by louis383 on 2018/8/29.
 */

val appModule = module {
    factory { EventTracker(androidApplication()) }
    factory { UserPreferenceManager(androidApplication()) }
    factory { QuickChecker(androidApplication()) }

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
    viewModel { BookSearchViewModel(get(), get(), get(), get(), get()) }
}

val appModules = listOf(
    appModule,
    domainModule,
    dataModule
)
