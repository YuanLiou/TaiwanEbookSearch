package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.domain.repository.BookRepository
import com.rayliu.commonmain.domain.repository.SearchRecordRepository
import com.rayliu.commonmain.domain.search.SearchSnapshotNotFoundException

class GetSearchSnapshotUseCase(
    private val bookRepository: BookRepository,
    private val searchRecordRepository: SearchRecordRepository
) {
    suspend operator fun invoke(searchId: String): Result<BookStores> {
        val bookStores =
            bookRepository.getSearchSnapshot(searchId).getOrElse {
                return Result.failure(it)
            }
        if (bookStores.searchId.isBlank() || bookStores.searchKeyword.isBlank()) {
            return Result.failure(SearchSnapshotNotFoundException())
        }
        saveKeyword(bookStores.searchKeyword)
        return Result.success(bookStores)
    }

    private suspend fun saveKeyword(keyword: String) {
        searchRecordRepository.saveKeywordToLocal(keyword)
    }
}
