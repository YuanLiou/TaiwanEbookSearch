package com.rayliu.commonmain.di

import com.rayliu.commonmain.LevenshteinDistanceHelper
import com.rayliu.commonmain.LevenshteinDistanceHelperImpl
import com.rayliu.commonmain.OffsetDateTimeHelper
import com.rayliu.commonmain.data.dao.SearchRecordDao
import com.rayliu.commonmain.data.dao.SearchRecordDaoImpl
import com.rayliu.commonmain.data.database.EbookTwDatabase
import com.rayliu.commonmain.data.mapper.BookDataMapper
import com.rayliu.commonmain.data.mapper.BookListMapper
import com.rayliu.commonmain.data.mapper.BookStoreDetailsMapper
import com.rayliu.commonmain.data.mapper.BookStoreMapper
import com.rayliu.commonmain.data.mapper.BookStoresMapper
import com.rayliu.commonmain.data.mapper.LocalSearchRecordMapper
import com.rayliu.commonmain.data.mapper.NetworkBookStoreListToBookStoreDetailListMapper
import com.rayliu.commonmain.data.mapper.NetworkResultToBookStoreListMapper
import com.rayliu.commonmain.data.mapper.SearchRecordMapper
import com.rayliu.commonmain.data.mapper.SearchResultMapper
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
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

val sharedModule = module {
    // Dispatchers
    factory<CoroutineDispatcher>(named("IO")) { Dispatchers.IO }
    factory<CoroutineDispatcher>(named("Default")) { Dispatchers.Default }

    // Json
    single<Json> {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }
    }

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

    // UseCases
    factory { GetBooksWithStoresUseCase(get(), get()) }
    factory<GetSearchRecordsUseCase> { GetSearchRecordsUseCase(get<SearchRecordRepository>()::getPagingSearchRecordsFactory) }
    factory<GetSearchRecordsCountsUseCase> { GetSearchRecordsCountsUseCase(get<SearchRecordRepository>()::getSearchRecordsCounts) }
    factory { DeleteSearchRecordUseCase(get<SearchRecordRepository>()) }
    factory { DeleteAllSearchRecordUseCase(get<SearchRecordRepository>()) }
    factory<GetDefaultBookSortUseCase> { GetDefaultBookSortUseCase(get()) }
    factory { SaveDefaultBookSortUseCase(get()) }
    factory { GetSearchSnapshotUseCase(get(), get()) }
    factory<GetIsUserSeenRankWindowUseCase> { GetIsUserSeenRankWindowUseCase(get()) }
    factory<SaveUserHasSeenRankWindowUseCase> { SaveUserHasSeenRankWindowUseCase(get()) }
    factory<GetBookStoresDetailUseCase> { GetBookStoresDetailUseCase(get()) }

    // Misc
    factory<LevenshteinDistanceHelper> { LevenshteinDistanceHelperImpl() }
    factory { OffsetDateTimeHelper() }
    
    // Database
    single { EbookTwDatabase(get()) }
}
