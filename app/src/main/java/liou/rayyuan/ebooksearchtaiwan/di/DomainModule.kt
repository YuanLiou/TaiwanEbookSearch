package liou.rayyuan.ebooksearchtaiwan.di

import liou.rayyuan.ebooksearchtaiwan.model.APIManager
import liou.rayyuan.ebooksearchtaiwan.model.data.mapper.*
import liou.rayyuan.ebooksearchtaiwan.model.domain.repository.BookRepositoryImpl
import liou.rayyuan.ebooksearchtaiwan.model.domain.usecase.GetBooksUseCase
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

    // Repositories
    factory {
        BookRepositoryImpl(get<APIManager>().bookSearchService, get<SearchResultMapper>())
    }

    // UseCases
    factory {
        GetBooksUseCase(get<BookRepositoryImpl>())
    }
}