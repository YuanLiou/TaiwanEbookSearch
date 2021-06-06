package liou.rayyuan.ebooksearchtaiwan.model.domain.usecase

import liou.rayyuan.ebooksearchtaiwan.model.domain.Result
import liou.rayyuan.ebooksearchtaiwan.model.domain.SimpleResult
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookStores
import liou.rayyuan.ebooksearchtaiwan.model.domain.repository.BookRepository
import liou.rayyuan.ebooksearchtaiwan.model.data.DefaultStoreNames

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