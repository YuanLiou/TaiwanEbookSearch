package liou.rayyuan.ebooksearchtaiwan.di

import androidx.room.Room
import com.rayliu.commonmain.domain.service.DatabaseManager
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import com.rayliu.commonmain.data.mapper.*
import com.rayliu.commonmain.data.api.BookSearchService
import com.rayliu.commonmain.data.dao.SearchRecordDao
import com.rayliu.commonmain.domain.repository.BookRepository
import com.rayliu.commonmain.domain.repository.BookRepositoryImpl
import com.rayliu.commonmain.domain.repository.BrowseHistoryRepository
import com.rayliu.commonmain.domain.repository.BrowseHistoryRepositoryImpl
import com.rayliu.commonmain.domain.repository.SearchRecordRepository
import com.rayliu.commonmain.domain.repository.SearchRecordRepositoryImpl
import com.rayliu.commonmain.domain.usecase.*
import com.rayliu.commonmain.userDataStore
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val domainModule = module {
    // Mappers
    factory {
        BookDataMapper()
    }

    factory {
        BookListMapper(get<BookDataMapper>())
    }

    factory {
        BookStoreDetailsMapper()
    }

    factory {
        BookStoreMapper(get<BookListMapper>(), get<BookStoreDetailsMapper>())
    }

    factory {
        BookStoreListMapper(get<BookStoreMapper>())
    }

    factory {
        SearchResultMapper(get<BookStoreListMapper>())
    }

    factory {
        BookStoresMapper(get<SearchResultMapper>())
    }

    factory {
        SearchRecordMapper()
    }

    factory {
        LocalSearchRecordMapper()
    }

    // Repositories
    factory<BookRepository> {
        BookRepositoryImpl(
            get<BookSearchService>(),
            get<BookStoresMapper>(),
            androidContext().userDataStore
        )
    }

    single {
        get<DatabaseManager>().searchRecordDao()
    }

    factory<SearchRecordRepository> {
        SearchRecordRepositoryImpl(get<SearchRecordMapper>(), get<LocalSearchRecordMapper>(), get<SearchRecordDao>())
    }

    factory<BrowseHistoryRepository> {
        BrowseHistoryRepositoryImpl(androidContext().userDataStore)
    }

    // UseCases
    factory {
        GetBooksWithStoresUseCase(get<BookRepository>(), get<SearchRecordRepository>())
    }

    factory<GetSearchRecordsUseCase> {
        GetSearchRecordsUseCase(get<SearchRecordRepository>()::getPagingSearchRecordsFactory)
    }

    factory<GetSearchRecordsCountsUseCase> {
        GetSearchRecordsCountsUseCase(get<SearchRecordRepository>()::getSearchRecordsCounts)
    }

    factory {
        DeleteSearchRecordUseCase(get<SearchRecordRepository>())
    }

    factory<GetDefaultBookSortUseCase> {
        GetDefaultBookSortUseCase(get<BookRepository>()::getDefaultResultSort)
    }

    factory {
        SaveDefaultBookSortUseCase(get<BookRepository>())
    }

    factory {
        GetSearchSnapshotUseCase(get<BookRepository>(), get<SearchRecordRepository>())
    }

    factory<GetIsUserSeenRankWindowUseCase> {
        GetIsUserSeenRankWindowUseCase(get<BrowseHistoryRepository>()::isUserSeenRankWindow)
    }

    factory<SaveUserHasSeenRankWindowUseCase> {
        SaveUserHasSeenRankWindowUseCase(get<BrowseHistoryRepository>()::setUserHasSeenRankWindow)
    }

    // Service (Application)
    // Database related and Daos
    single {
        Room.databaseBuilder(androidApplication(),
            DatabaseManager::class.java,
            DatabaseManager.DATABASE_NAME)
            .build()
    }

    factory { UserPreferenceManager(androidApplication()) }
}