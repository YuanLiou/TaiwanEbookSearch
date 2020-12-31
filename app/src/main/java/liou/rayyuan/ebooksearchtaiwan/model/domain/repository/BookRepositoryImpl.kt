package liou.rayyuan.ebooksearchtaiwan.model.domain.repository

import liou.rayyuan.ebooksearchtaiwan.model.APIManager
import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkBookStores
import liou.rayyuan.ebooksearchtaiwan.model.data.mapper.basic.Mapper
import liou.rayyuan.ebooksearchtaiwan.model.domain.Result
import liou.rayyuan.ebooksearchtaiwan.model.domain.SimpleResult
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookStores

class BookRepositoryImpl(
    private val apiManager: APIManager,
    private val bookStoreDataMapper: Mapper<NetworkBookStores, BookStores>
) : BookRepository {

    override suspend fun getBooks(keyword: String): SimpleResult<BookStores> {
        val bookStoreResponse = apiManager.getBooks(keyword)
        val body = bookStoreResponse.body()
        return if (bookStoreResponse.isSuccessful && body != null) {
            Result.Success(mapBookStores(body))
        } else {
            Result.Failed(BookResultException("Response is failed, code is ${bookStoreResponse.code()}"))
        }
    }

    private fun mapBookStores(input: NetworkBookStores): BookStores {
        return bookStoreDataMapper.map(input)
    }

    class BookResultException(message: String) : Throwable(message)
}