package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.domain.repository.BookRepository
import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.repository.SearchRecordRepository

class GetBooksWithStoresUseCase(
    private val bookRepository: BookRepository,
    private val searchRecordRepository: SearchRecordRepository
) {

    suspend operator fun invoke(bookStores: List<DefaultStoreNames>, keyword: String): Result<BookStores> {
        saveKeyword(keyword)
        return bookRepository.getBooksWithStores(bookStores, keyword)
    }

    private suspend fun saveKeyword(keyword: String) {
        searchRecordRepository.saveKeywordToLocal(keyword)
    }
}