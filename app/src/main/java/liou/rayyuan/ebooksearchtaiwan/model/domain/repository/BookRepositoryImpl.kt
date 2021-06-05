package liou.rayyuan.ebooksearchtaiwan.model.domain.repository

import liou.rayyuan.ebooksearchtaiwan.model.data.api.BookSearchService
import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkCrawerResult
import liou.rayyuan.ebooksearchtaiwan.model.data.mapper.BookStoresMapper
import liou.rayyuan.ebooksearchtaiwan.model.domain.Result
import liou.rayyuan.ebooksearchtaiwan.model.domain.SimpleResult
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookStores
import liou.rayyuan.ebooksearchtaiwan.utils.DefaultStoreNames

class BookRepositoryImpl(
    private val bookSearchService: BookSearchService,
    private val bookStoresMapper: BookStoresMapper
) : BookRepository {

    override suspend fun getBooks(keyword: String): SimpleResult<BookStores> {
        return try {
            val response = bookSearchService.postBooks(keyword)
            Result.Success(mapBookStores(response))
        } catch (exception: Exception) {
            Result.Failed(BookResultException("Response is failed, exception is $exception", exception))
        }
    }

    override suspend fun getBooksWithStores(
        stores: List<DefaultStoreNames>,
        keyword: String
    ): SimpleResult<BookStores> {
        val storeStringList = stores.map { it.defaultName }
        return try {
            val response = bookSearchService.postBooks(storeStringList, keyword)
            Result.Success(mapBookStores(response))
        } catch (exception: Exception) {
            Result.Failed(BookResultException("Response is failed, exception is $exception", exception))
        }
    }

    private fun mapBookStores(input: NetworkCrawerResult): BookStores {
        return bookStoresMapper.map(input)
    }

    class BookResultException(message: String, exception: Exception) : Throwable(message)
}