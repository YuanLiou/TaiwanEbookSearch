package com.rayliu.commonmain.domain.repository

import com.rayliu.commonmain.data.api.BookStoresService
import com.rayliu.commonmain.data.mapper.NetworkBookStoreListToBookStoreDetailListMapper
import com.rayliu.commonmain.domain.model.BookStoreDetails
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.firstOrNull

class BookStoreDetailsRepositoryImpl(
    private val bookSearchService: BookStoresService,
    private val bookRepository: BookRepository,
    private val bookStoresDetailMapper: NetworkBookStoreListToBookStoreDetailListMapper,
) : BookStoreDetailsRepository {
    override suspend fun getBookStoresDetail(): Result<ImmutableList<BookStoreDetails>> =
        runCatching {
            val source = bookSearchService.getBookStores()
            val enableStores = bookRepository.getDefaultResultSort().firstOrNull().orEmpty()
            bookStoresDetailMapper.setEnableStores(enableStores)
            bookStoresDetailMapper.map(source).toImmutableList()
        }
}
