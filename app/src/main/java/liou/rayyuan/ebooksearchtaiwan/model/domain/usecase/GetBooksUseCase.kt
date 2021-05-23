package liou.rayyuan.ebooksearchtaiwan.model.domain.usecase

import liou.rayyuan.ebooksearchtaiwan.model.domain.Result
import liou.rayyuan.ebooksearchtaiwan.model.domain.SimpleResult
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookStores
import liou.rayyuan.ebooksearchtaiwan.model.domain.repository.BookRepository

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