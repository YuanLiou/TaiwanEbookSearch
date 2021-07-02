package liou.rayyuan.ebooksearchtaiwan.di

import androidx.room.Room
import com.rayliu.commonmain.domain.service.DatabaseManager
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import com.rayliu.commonmain.data.mapper.*
import com.rayliu.commonmain.data.api.BookSearchApi
import com.rayliu.commonmain.data.dao.SearchRecordDao
import com.rayliu.commonmain.domain.repository.BookRepository
import com.rayliu.commonmain.domain.repository.BookRepositoryImpl
import com.rayliu.commonmain.domain.repository.SearchRecordRepository
import com.rayliu.commonmain.domain.repository.SearchRecordRepositoryImpl
import com.rayliu.commonmain.domain.usecase.*
import org.koin.android.ext.koin.androidApplication
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
        BookRepositoryImpl(get<BookSearchApi>(), get<BookStoresMapper>(), get<UserPreferenceManager>())
    }

    single {
        get<DatabaseManager>().searchRecordDao()
    }

    factory<SearchRecordRepository> {
        SearchRecordRepositoryImpl(get<SearchRecordMapper>(), get<LocalSearchRecordMapper>(), get<SearchRecordDao>())
    }

    // UseCases
    factory {
        GetBooksWithStoresUseCase(get<BookRepository>(), get<SearchRecordRepository>())
    }

    factory {
        GetSearchRecordsUseCase(get<SearchRecordRepository>())
    }

    factory {
        GetSearchRecordsCountsUseCase(get<SearchRecordRepository>())
    }

    factory {
        DeleteSearchRecordUseCase(get<SearchRecordRepository>())
    }

    factory {
        GetDefaultBookSortUseCase(get<BookRepository>())
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