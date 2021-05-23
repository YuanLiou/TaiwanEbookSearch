package liou.rayyuan.ebooksearchtaiwan.model.domain.repository

import liou.rayyuan.ebooksearchtaiwan.model.APIManager
import liou.rayyuan.ebooksearchtaiwan.model.data.BookStoreKeys
import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkCrawerResult
import liou.rayyuan.ebooksearchtaiwan.model.data.mapper.SearchResultMapper
import liou.rayyuan.ebooksearchtaiwan.model.domain.Result
import liou.rayyuan.ebooksearchtaiwan.model.domain.SimpleResult
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookStores

class BookRepositoryImpl(
    private val apiManager: APIManager,
    private val searchResultMapper: SearchResultMapper
) : BookRepository {

    override suspend fun getBooks(keyword: String): SimpleResult<BookStores> {
        val bookStoreResponse = apiManager.postBooks(keyword)
        val body = bookStoreResponse.body()
        return if (bookStoreResponse.isSuccessful && body != null) {
            Result.Success(mapBookStores(body))
        } else {
            Result.Failed(BookResultException("Response is failed, code is ${bookStoreResponse.code()}"))
        }
    }

    private fun mapBookStores(input: NetworkCrawerResult): BookStores {
        val convertedResult = searchResultMapper.map(input)
        val books = convertedResult.bookStores.associate {
            Pair(it.bookStoreDetails?.id, it.books)
        }

        return BookStores(
            booksCompany = books[BookStoreKeys.booksCompany],
            readmoo = books[BookStoreKeys.readmoo],
            kobo = books[BookStoreKeys.kobo],
            taaze = books[BookStoreKeys.taaze],
            bookWalker = books[BookStoreKeys.bookwalker],
            playStore = books[BookStoreKeys.playStore],
            pubu = books[BookStoreKeys.pubu],
            hyread = books[BookStoreKeys.hyread],
            kindle = books[BookStoreKeys.kindle]
        )
    }

    class BookResultException(message: String) : Throwable(message)
}