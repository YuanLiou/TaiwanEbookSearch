package com.rayliu.commonmain.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rayliu.commonmain.OffsetDateTimeHelper
import com.rayliu.commonmain.data.DataStoreFactory
import com.rayliu.commonmain.data.api.BookSearchService
import com.rayliu.commonmain.data.api.BookStoresService
import com.rayliu.commonmain.data.dao.SearchRecordDao
import com.rayliu.commonmain.data.dao.SearchRecordDaoImpl
import com.rayliu.commonmain.data.database.EbookTwDatabase
import com.rayliu.commonmain.data.mapper.BookDataMapper
import com.rayliu.commonmain.data.mapper.BookListMapper
import com.rayliu.commonmain.data.mapper.BookStoreDetailsMapper
import com.rayliu.commonmain.data.mapper.NetworkResultToBookStoreListMapper
import com.rayliu.commonmain.data.mapper.BookStoreMapper
import com.rayliu.commonmain.data.mapper.BookStoresMapper
import com.rayliu.commonmain.data.mapper.LocalSearchRecordMapper
import com.rayliu.commonmain.data.mapper.NetworkBookStoreListToBookStoreDetailListMapper
import com.rayliu.commonmain.data.mapper.SearchRecordMapper
import com.rayliu.commonmain.data.mapper.SearchResultMapper
import com.rayliu.commonmain.domain.repository.BookRepository
import com.rayliu.commonmain.domain.repository.BookRepositoryImpl
import com.rayliu.commonmain.domain.repository.BookStoreDetailsRepository
import com.rayliu.commonmain.domain.repository.BookStoreDetailsRepositoryImpl
import com.rayliu.commonmain.domain.repository.BrowseHistoryRepository
import com.rayliu.commonmain.domain.repository.BrowseHistoryRepositoryImpl
import com.rayliu.commonmain.domain.repository.SearchRecordRepository
import com.rayliu.commonmain.domain.repository.SearchRecordRepositoryImpl
import com.rayliu.commonmain.domain.usecase.DeleteAllSearchRecordUseCase
import com.rayliu.commonmain.domain.usecase.DeleteSearchRecordUseCase
import com.rayliu.commonmain.domain.usecase.GetBookStoresDetailUseCase
import com.rayliu.commonmain.domain.usecase.GetBooksWithStoresUseCase
import com.rayliu.commonmain.domain.usecase.GetDefaultBookSortUseCase
import com.rayliu.commonmain.domain.usecase.GetIsUserSeenRankWindowUseCase
import com.rayliu.commonmain.domain.usecase.GetSearchRecordsCountsUseCase
import com.rayliu.commonmain.domain.usecase.GetSearchRecordsUseCase
import com.rayliu.commonmain.domain.usecase.GetSearchSnapshotUseCase
import com.rayliu.commonmain.domain.usecase.SaveDefaultBookSortUseCase
import com.rayliu.commonmain.domain.usecase.SaveUserHasSeenRankWindowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val commonModule = module {
    // Mappers
    factory { BookDataMapper(get()) }
    factory { BookListMapper(get<BookDataMapper>()) }
    factory { BookStoreDetailsMapper() }
    factory { BookStoreMapper(get<BookListMapper>(), get<BookStoreDetailsMapper>()) }
    factory { NetworkResultToBookStoreListMapper(get<BookStoreMapper>()) }
    factory { SearchResultMapper(get<NetworkResultToBookStoreListMapper>()) }
    factory { NetworkBookStoreListToBookStoreDetailListMapper(get<BookStoreDetailsMapper>()) }
    factory { BookStoresMapper(get<SearchResultMapper>()) }
    factory { SearchRecordMapper() }
    factory { LocalSearchRecordMapper(get<OffsetDateTimeHelper>()) }

    // Repositories
    factory<BookRepository> {
        BookRepositoryImpl(
            get<BookSearchService>(),
            get<BookStoresMapper>(),
            get<DataStore<Preferences>>()
        )
    }
    factory<BookStoreDetailsRepository> {
        BookStoreDetailsRepositoryImpl(
            get<BookStoresService>(),
            get<BookRepository>(),
            get<NetworkBookStoreListToBookStoreDetailListMapper>()
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
    factory<BrowseHistoryRepository> { BrowseHistoryRepositoryImpl(get<DataStore<Preferences>>()) }

    // UseCases
    factory { GetBooksWithStoresUseCase(get<BookRepository>(), get<SearchRecordRepository>()) }
    factory<GetSearchRecordsUseCase> { GetSearchRecordsUseCase(get<SearchRecordRepository>()::getPagingSearchRecordsFactory) }
    factory<GetSearchRecordsCountsUseCase> { GetSearchRecordsCountsUseCase(get<SearchRecordRepository>()::getSearchRecordsCounts) }
    factory { DeleteSearchRecordUseCase(get<SearchRecordRepository>()) }
    factory { DeleteAllSearchRecordUseCase(get<SearchRecordRepository>()) }
    factory<GetDefaultBookSortUseCase> { GetDefaultBookSortUseCase(get<BookRepository>()::getDefaultResultSort) }
    factory { SaveDefaultBookSortUseCase(get<BookRepository>()) }
    factory { GetSearchSnapshotUseCase(get<BookRepository>(), get<SearchRecordRepository>()) }
    factory<GetIsUserSeenRankWindowUseCase> { GetIsUserSeenRankWindowUseCase(get<BrowseHistoryRepository>()::isUserSeenRankWindow) }
    factory<SaveUserHasSeenRankWindowUseCase> { SaveUserHasSeenRankWindowUseCase(get<BrowseHistoryRepository>()::setUserHasSeenRankWindow) }
    factory<GetBookStoresDetailUseCase> { GetBookStoresDetailUseCase(get<BookStoreDetailsRepository>()::getBookStoresDetail) }

    // Database
    single<EbookTwDatabase> { EbookTwDatabase(get()) }

    // DataStore
    single<DataStore<Preferences>> { get<DataStoreFactory>().createDataStore() }
}

expect val platformModule: Module
