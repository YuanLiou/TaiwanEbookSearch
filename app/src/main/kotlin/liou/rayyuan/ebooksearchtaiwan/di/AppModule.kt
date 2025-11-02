package liou.rayyuan.ebooksearchtaiwan.di

import com.rayliu.commonmain.data.api.BookSearchService
import com.rayliu.commonmain.data.api.BookStoresService
import com.rayliu.commonmain.di.DriverFactory
import com.rayliu.commonmain.domain.repository.BookRepository
import com.rayliu.commonmain.domain.repository.BookRepositoryImpl
import com.rayliu.commonmain.domain.repository.BookStoreDetailsRepository
import com.rayliu.commonmain.domain.repository.BookStoreDetailsRepositoryImpl
import com.rayliu.commonmain.domain.repository.BrowseHistoryRepository
import com.rayliu.commonmain.domain.repository.BrowseHistoryRepositoryImpl
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import com.rayliu.commonmain.userDataStore
import liou.rayyuan.ebooksearchtaiwan.camerapreview.preview.CameraPreviewViewModel
import liou.rayyuan.ebooksearchtaiwan.camerapreview.usecase.CameraUseCase
import liou.rayyuan.ebooksearchtaiwan.camerapreview.usecase.CameraXUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule =
    module {
        // Repositories
        factory<BookRepository> {
            BookRepositoryImpl(
                get<BookSearchService>(),
                get(),
                androidContext().userDataStore
            )
        }

        factory<BookStoreDetailsRepository> {
            BookStoreDetailsRepositoryImpl(
                get<BookStoresService>(),
                get<BookRepository>(),
                get()
            )
        }

        factory<BrowseHistoryRepository> {
            BrowseHistoryRepositoryImpl(androidContext().userDataStore)
        }

        // Platform-specific
        factory { UserPreferenceManager(androidApplication()) }

        single { DriverFactory(androidContext()).createDriver() }

        // Barcode Scanner
        viewModel {
            CameraPreviewViewModel(
                cameraUseCase = get(),
                resourceHelper = get()
            )
        }
        factory<CameraUseCase> {
            CameraXUseCase(
                application = androidApplication()
            )
        }
    }
