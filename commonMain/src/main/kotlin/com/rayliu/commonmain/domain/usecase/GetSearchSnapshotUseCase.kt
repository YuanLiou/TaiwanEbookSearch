package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.domain.repository.BookRepository
import com.rayliu.commonmain.domain.repository.SearchRecordRepository

class GetSearchSnapshotUseCase(
    private val bookRepository: BookRepository,
    private val searchRecordRepository: SearchRecordRepository
) {
    suspend operator fun invoke(searchId: String): Result<BookStores> =
        bookRepository.getSearchSnapshot(searchId).onSuccess {
            val keyword = it.searchKeyword
            if (keyword.isNotEmpty()) {
                saveKeyword(keyword)
            }
        }

    private suspend fun saveKeyword(keyword: String) {
        searchRecordRepository.saveKeywordToLocal(keyword)
    }
}
