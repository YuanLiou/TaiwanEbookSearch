package com.rayliu.commonmain.domain.repository

import com.rayliu.commonmain.data.api.BookStoresService
import com.rayliu.commonmain.data.mapper.NetworkResultToBookStoreListMapper
import com.rayliu.commonmain.domain.model.BookStore

class BookStoresRepositoryImpl(
    private val bookSearchService: BookStoresService,
    private val bookStoresMapper: NetworkResultToBookStoreListMapper,
) : BookStoresRepository {
    override suspend fun getBookStores(): Result<List<BookStore>> {
        // TODO
        return Result.success(emptyList())
    }
}
