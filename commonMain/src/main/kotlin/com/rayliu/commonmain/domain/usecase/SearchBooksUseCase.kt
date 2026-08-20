package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.model.BookSearchSlice
import com.rayliu.commonmain.domain.search.BlankSearchQueryException
import com.rayliu.commonmain.domain.search.BookStoresResultSlicer
import com.rayliu.commonmain.domain.search.NetworkUnavailableException
import com.rayliu.commonmain.domain.service.NetworkAvailability
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import kotlinx.coroutines.flow.first

class SearchBooksUseCase(
    private val getDefaultBookSortUseCase: GetDefaultBookSortUseCase,
    private val getBooksWithStoresUseCase: GetBooksWithStoresUseCase,
    private val networkAvailability: NetworkAvailability,
    private val userPreferenceManager: UserPreferenceManager
) {
    suspend operator fun invoke(query: String): Result<BookSearchSlice> {
        if (query.trim().isBlank()) {
            return Result.failure(BlankSearchQueryException())
        }

        if (!networkAvailability.isAvailable()) {
            return Result.failure(NetworkUnavailableException())
        }

        val enabledStores = getDefaultBookSortUseCase().first()
        val bookStores =
            getBooksWithStoresUseCase(enabledStores, query).getOrElse {
                return Result.failure(it)
            }

        return Result.success(
            BookStoresResultSlicer.slice(
                bookStores = bookStores,
                enabledStores = enabledStores,
                sortStoreBooksByPrice = userPreferenceManager.isSearchResultSortByPrice()
            )
        )
    }
}
