package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.domain.repository.BookRepository
import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.repository.SearchRecordRepository
import kotlinx.collections.immutable.ImmutableList

class GetBooksWithStoresUseCase(
    private val bookRepository: BookRepository,
    private val searchRecordRepository: SearchRecordRepository
) {
    suspend operator fun invoke(
        bookStores: ImmutableList<DefaultStoreNames>,
        keyword: String
    ): Result<BookStores> {
        saveKeyword(keyword)
        return bookRepository.getBooksWithStores(bookStores, keyword)
    }

    private suspend fun saveKeyword(keyword: String) {
        searchRecordRepository.saveKeywordToLocal(keyword)
    }
}
