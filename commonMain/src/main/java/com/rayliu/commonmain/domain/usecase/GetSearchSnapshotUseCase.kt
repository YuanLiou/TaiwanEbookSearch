package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.Result
import com.rayliu.commonmain.domain.SimpleResult
import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.domain.repository.BookRepository
import com.rayliu.commonmain.domain.repository.SearchRecordRepository

class GetSearchSnapshotUseCase(
    private val bookRepository: BookRepository,
    private val searchRecordRepository: SearchRecordRepository
) {

    suspend operator fun invoke(searchId: String): SimpleResult<BookStores> {
        val result = bookRepository.getSearchSnapshot(searchId)
        return when (result) {
            is Result.Success -> {
                val keyword = result.value.searchKeyword
                if (keyword.isNotEmpty()) {
                    saveKeyword(keyword)
                }
                Result.Success(result.value)
            }
            is Result.Failed -> Result.Failed(result.error)
        }
    }

    private suspend fun saveKeyword(keyword: String) {
        searchRecordRepository.saveKeywordToLocal(keyword)
    }
}
