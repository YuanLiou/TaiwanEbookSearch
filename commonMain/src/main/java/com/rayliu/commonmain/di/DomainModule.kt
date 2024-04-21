package com.rayliu.commonmain.di

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.rayliu.commonmain.OffsetDateTimeHelper
import com.rayliu.commonmain.data.api.BookSearchService
import com.rayliu.commonmain.data.dao.SearchRecordDao
import com.rayliu.commonmain.data.dao.SearchRecordDaoImpl
import com.rayliu.commonmain.data.database.EbookTwDatabase
import com.rayliu.commonmain.data.mapper.BookDataMapper
import com.rayliu.commonmain.data.mapper.BookListMapper
import com.rayliu.commonmain.data.mapper.BookStoreDetailsMapper
import com.rayliu.commonmain.data.mapper.BookStoreListMapper
import com.rayliu.commonmain.data.mapper.BookStoreMapper
import com.rayliu.commonmain.data.mapper.BookStoresMapper
import com.rayliu.commonmain.data.mapper.LocalSearchRecordMapper
import com.rayliu.commonmain.data.mapper.SearchRecordMapper
import com.rayliu.commonmain.data.mapper.SearchResultMapper
import com.rayliu.commonmain.domain.repository.BookRepository
import com.rayliu.commonmain.domain.repository.BookRepositoryImpl
import com.rayliu.commonmain.domain.repository.BrowseHistoryRepository
import com.rayliu.commonmain.domain.repository.BrowseHistoryRepositoryImpl
import com.rayliu.commonmain.domain.repository.SearchRecordRepository
import com.rayliu.commonmain.domain.repository.SearchRecordRepositoryImpl
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import com.rayliu.commonmain.domain.usecase.DeleteSearchRecordUseCase
import com.rayliu.commonmain.domain.usecase.GetBooksWithStoresUseCase
import com.rayliu.commonmain.domain.usecase.GetDefaultBookSortUseCase
import com.rayliu.commonmain.domain.usecase.GetIsUserSeenRankWindowUseCase
import com.rayliu.commonmain.domain.usecase.GetSearchRecordsCountsUseCase
import com.rayliu.commonmain.domain.usecase.GetSearchRecordsUseCase
import com.rayliu.commonmain.domain.usecase.GetSearchSnapshotUseCase
import com.rayliu.commonmain.domain.usecase.SaveDefaultBookSortUseCase
import com.rayliu.commonmain.domain.usecase.SaveUserHasSeenRankWindowUseCase
import com.rayliu.commonmain.userDataStore
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val DATABASE_NAME = "ebooktw_database"
val domainModule =
    module {
        // Mappers
        factory {
            BookDataMapper(get())
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
            LocalSearchRecordMapper(get<OffsetDateTimeHelper>())
        }

        // Repositories
        factory<BookRepository> {
            BookRepositoryImpl(
                get<BookSearchService>(),
                get<BookStoresMapper>(),
                androidContext().userDataStore
            )
        }

        single<SearchRecordDao> {
            SearchRecordDaoImpl(
                get<OffsetDateTimeHelper>(),
                get<SearchRecordMapper>(),
                get<CoroutineDispatcher>(qualifier = named("IO")),
                get<CoroutineDispatcher>(qualifier = named("Default")),
                get<EbookTwDatabase>()
            )
        }

        factory<SearchRecordRepository> {
            SearchRecordRepositoryImpl(
                get<OffsetDateTimeHelper>(),
                get<LocalSearchRecordMapper>(),
                get<SearchRecordDao>()
            )
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
        single<EbookTwDatabase> {
            val driver =
                AndroidSqliteDriver(
                    schema = EbookTwDatabase.Schema,
                    context = androidContext(),
                    name = DATABASE_NAME
                )
            EbookTwDatabase(driver)
        }

        factory { UserPreferenceManager(androidApplication()) }
    }
