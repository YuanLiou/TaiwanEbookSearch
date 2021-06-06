package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.Result
import com.rayliu.commonmain.domain.SimpleResult
import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.domain.repository.BookRepository
import com.rayliu.commonmain.data.DefaultStoreNames

class GetBooksWithStoresUseCase(
    private val bookRepository: BookRepository
) {

    suspend operator fun invoke(bookStores: List<DefaultStoreNames>, keyword: String): SimpleResult<BookStores> {
        val result = bookRepository.getBooksWithStores(bookStores, keyword)
        return when (result) {
            is Result.Success -> Result.Success(result.value)
            is Result.Failed -> Result.Failed(result.error)
        }
    }
}