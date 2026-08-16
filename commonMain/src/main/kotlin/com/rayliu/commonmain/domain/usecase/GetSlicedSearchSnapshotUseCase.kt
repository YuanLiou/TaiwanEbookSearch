package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.model.BookSearchSlice
import com.rayliu.commonmain.domain.search.BlankSearchIdException
import com.rayliu.commonmain.domain.search.BookStoresResultSlicer
import com.rayliu.commonmain.domain.search.NetworkUnavailableException
import com.rayliu.commonmain.domain.service.NetworkAvailability
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import kotlinx.coroutines.flow.first

class GetSlicedSearchSnapshotUseCase(
    private val getSearchSnapshotUseCase: GetSearchSnapshotUseCase,
    private val getDefaultBookSortUseCase: GetDefaultBookSortUseCase,
    private val networkAvailability: NetworkAvailability,
    private val userPreferenceManager: UserPreferenceManager
) {
    suspend operator fun invoke(searchId: String): Result<BookSearchSlice> {
        if (searchId.trim().isBlank()) {
            return Result.failure(BlankSearchIdException())
        }

        if (!networkAvailability.isAvailable()) {
            return Result.failure(NetworkUnavailableException())
        }

        val bookStores =
            getSearchSnapshotUseCase(searchId).getOrElse {
                return Result.failure(it)
            }
        val enabledStores = getDefaultBookSortUseCase().first()

        return Result.success(
            BookStoresResultSlicer.slice(
                bookStores = bookStores,
                enabledStores = enabledStores,
                sortStoreBooksByPrice = userPreferenceManager.isSearchResultSortByPrice()
            )
        )
    }
}
