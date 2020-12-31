package liou.rayyuan.ebooksearchtaiwan.di

import liou.rayyuan.ebooksearchtaiwan.model.data.mapper.BookDataMapper
import liou.rayyuan.ebooksearchtaiwan.model.data.mapper.BookStoreDataMapper
import liou.rayyuan.ebooksearchtaiwan.model.domain.repository.BookRepositoryImpl
import liou.rayyuan.ebooksearchtaiwan.model.domain.usecase.GetBooksUseCase
import org.koin.dsl.module


val domainModule = module {
    // Mappers
    factory {
        BookDataMapper()
    }

    factory {
        BookStoreDataMapper(get())
    }

    // Repositories
    factory {
        BookRepositoryImpl(get(), get<BookStoreDataMapper>())
    }

    // UseCases
    factory {
        GetBooksUseCase(get<BookRepositoryImpl>())
    }
}