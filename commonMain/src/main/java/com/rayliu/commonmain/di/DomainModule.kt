package liou.rayyuan.ebooksearchtaiwan.di

import androidx.room.Room
import com.rayliu.commonmain.DatabaseManager
import com.rayliu.commonmain.UserPreferenceManager
import com.rayliu.commonmain.data.mapper.*
import com.rayliu.commonmain.data.api.BookSearchApi
import com.rayliu.commonmain.domain.repository.BookRepositoryImpl
import com.rayliu.commonmain.domain.usecase.GetBooksUseCase
import com.rayliu.commonmain.domain.usecase.GetBooksWithStoresUseCase
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

    // Repositories
    factory {
        BookRepositoryImpl(get<BookSearchApi>(), get<BookStoresMapper>())
    }

    single {
        get<DatabaseManager>().searchRecordDao()
    }

    // UseCases
    factory {
        GetBooksUseCase(get<BookRepositoryImpl>())
    }

    factory {
        GetBooksWithStoresUseCase(get<BookRepositoryImpl>())
    }

    // Services(Application)
    // Database related and Daos
    single {
        Room.databaseBuilder(androidApplication(),
            DatabaseManager::class.java,
            DatabaseManager.DATABASE_NAME)
            .build()
    }

    factory { UserPreferenceManager(androidApplication()) }
}