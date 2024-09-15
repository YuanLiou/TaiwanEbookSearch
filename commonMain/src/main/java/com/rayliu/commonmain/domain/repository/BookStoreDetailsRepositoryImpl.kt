package com.rayliu.commonmain.domain.repository

import com.rayliu.commonmain.data.api.BookStoresService
import com.rayliu.commonmain.data.mapper.NetworkBookStoreListToBookStoreDetailListMapper
import com.rayliu.commonmain.domain.model.BookStoreDetails
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class BookStoreDetailsRepositoryImpl(
    private val bookSearchService: BookStoresService,
    private val bookStoresDetailMapper: NetworkBookStoreListToBookStoreDetailListMapper,
) : BookStoreDetailsRepository {
    override suspend fun getBookStoresDetail(): Result<ImmutableList<BookStoreDetails>> =
        runCatching {
            val source = bookSearchService.getBookStores()
            bookStoresDetailMapper.map(source).toImmutableList()
        }
}
