package liou.rayyuan.ebooksearchtaiwan.di

import liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchViewModel
import liou.rayyuan.ebooksearchtaiwan.bookstorereorder.BookStoreReorderViewModel
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
    factory { QuickChecker(androidApplication()) }

    // ViewModels
    viewModel { BookSearchViewModel(
        getBooksWithStoresUseCase = get(),
        getSearchRecordsUseCase = get(),
        getSearchRecordsCountsUseCase = get(),
        getDefaultBookSortUseCase = get(),
        eventTracker = get(),
        quickChecker = get(),
        deleteSearchRecordUseCase = get()
    ) }

    viewModel {
        BookStoreReorderViewModel(
            getDefaultBookSortUseCase = get(),
            saveDefaultBookBookSortUseCase = get()
        )
    }
}

val appModules = listOf(
    appModule,
    domainModule,
    dataModule
)
