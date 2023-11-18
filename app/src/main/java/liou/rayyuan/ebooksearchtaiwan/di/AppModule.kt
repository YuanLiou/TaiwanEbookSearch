package liou.rayyuan.ebooksearchtaiwan.di

import android.content.ClipboardManager
import android.content.Context
import com.rayliu.commonmain.SystemInfoCollector
import com.rayliu.commonmain.di.dataModule
import com.rayliu.commonmain.di.domainModule
import com.rayliu.commonmain.di.jsonModule
import com.rayliu.commonmain.di.miscModule
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchViewModel
import liou.rayyuan.ebooksearchtaiwan.booksearch.review.PlayStoreReviewHelper
import liou.rayyuan.ebooksearchtaiwan.bookstorereorder.BookStoreReorderViewModel
import liou.rayyuan.ebooksearchtaiwan.interactor.UserRankingWindowFacade
import liou.rayyuan.ebooksearchtaiwan.model.EventTracker
import liou.rayyuan.ebooksearchtaiwan.utils.ClipboardHelper
import liou.rayyuan.ebooksearchtaiwan.utils.CustomTabSessionManager
import liou.rayyuan.ebooksearchtaiwan.utils.QuickChecker
import liou.rayyuan.ebooksearchtaiwan.utils.ResourceHelper
import liou.rayyuan.ebooksearchtaiwan.utils.SystemInfoCollectorImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by louis383 on 2018/8/29.
 */

val appModule = module {
    factory { EventTracker(androidApplication()) }

    // ViewModels
    viewModel {
        BookSearchViewModel(
            getBooksWithStoresUseCase = get(),
            getSearchRecordsUseCase = get(),
            getSearchRecordsCountsUseCase = get(),
            getDefaultBookSortUseCase = get(),
            getSearchSnapshotUseCase = get(),
            eventTracker = get(),
            quickChecker = get(),
            deleteSearchRecordUseCase = get(),
            resourceHelper = get(),
            rankingWindowFacade = get(),
            clipboardHelper = get(),
            userPreferenceManager = get()
        )
    }

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
    single {
        PlayStoreReviewHelper(androidContext())
    }
    factory {
        val clipboardManager = androidContext().getSystemService(Context.CLIPBOARD_SERVICE)
            as ClipboardManager
        ClipboardHelper(clipboardManager)
    }
    factory {
        CustomTabSessionManager(getDefaultBookSortUseCase = get())
    }
    factory<SystemInfoCollector> {
        SystemInfoCollectorImpl()
    }
}

val appModules = listOf(
    appModule,
    appUtilsModule,
    domainModule,
    dataModule,
    jsonModule,
    miscModule
)
