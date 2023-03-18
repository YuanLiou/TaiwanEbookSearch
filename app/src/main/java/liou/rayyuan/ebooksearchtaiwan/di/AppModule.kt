package liou.rayyuan.ebooksearchtaiwan.di

import liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchViewModel
import liou.rayyuan.ebooksearchtaiwan.bookstorereorder.BookStoreReorderViewModel
import liou.rayyuan.ebooksearchtaiwan.interactor.UserRankingWindowFacade
import liou.rayyuan.ebooksearchtaiwan.model.*
import liou.rayyuan.ebooksearchtaiwan.utils.QuickChecker
import liou.rayyuan.ebooksearchtaiwan.utils.ResourceHelper
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by louis383 on 2018/8/29.
 */

val appModule = module {
    factory { EventTracker(androidApplication()) }

    // ViewModels
    viewModel { BookSearchViewModel(
        getBooksWithStoresUseCase = get(),
        getSearchRecordsUseCase = get(),
        getSearchRecordsCountsUseCase = get(),
        getDefaultBookSortUseCase = get(),
        getSearchSnapshotUseCase = get(),
        eventTracker = get(),
        quickChecker = get(),
        deleteSearchRecordUseCase = get(),
        resourceHelper = get(),
        rankingWindowFacade = get()
    ) }

    viewModel {
        BookStoreReorderViewModel(
            getDefaultBookSortUseCase = get(),
            saveDefaultBookBookSortUseCase = get()
        )
    }

    // Interactors
    factory {
        UserRankingWindowFacade(
            isUserSeenRankWindow = get(),
            saveUserHasSeenRankWindow = get()
        )
    }
}

val appUtilsModule = module {
    factory { ResourceHelper(androidApplication()) }
    factory { QuickChecker(androidApplication()) }
}

val appModules = listOf(
    appModule,
    appUtilsModule,
    domainModule,
    dataModule
)
