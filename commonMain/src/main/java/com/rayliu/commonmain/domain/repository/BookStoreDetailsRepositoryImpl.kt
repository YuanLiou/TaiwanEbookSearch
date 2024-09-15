package com.rayliu.commonmain.domain.repository

import com.rayliu.commonmain.data.api.BookStoresService
import com.rayliu.commonmain.data.mapper.NetworkBookStoreListToBookStoreDetailListMapper
import com.rayliu.commonmain.domain.model.BookStoreDetails

class BookStoreDetailsRepositoryImpl(
    private val bookSearchService: BookStoresService,
    private val bookStoresDetailMapper: NetworkBookStoreListToBookStoreDetailListMapper,
) : BookStoreDetailsRepository {
    override suspend fun getBookStoresDetail(): Result<List<BookStoreDetails>> =
        runCatching {
            val source = bookSearchService.getBookStores()
            bookStoresDetailMapper.map(source)
        }
}
