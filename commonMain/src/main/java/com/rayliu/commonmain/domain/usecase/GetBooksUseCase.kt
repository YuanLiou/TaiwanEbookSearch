package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.Result
import com.rayliu.commonmain.domain.SimpleResult
import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.domain.repository.BookRepository

class GetBooksUseCase(
    private val bookRepository: BookRepository
) {

    suspend operator fun invoke(keyword: String): SimpleResult<BookStores> {
        val result = bookRepository.getBooks(keyword)
        return when (result) {
            is Result.Success -> Result.Success(result.value)
            is Result.Failed -> Result.Failed(result.error)
        }
    }
}