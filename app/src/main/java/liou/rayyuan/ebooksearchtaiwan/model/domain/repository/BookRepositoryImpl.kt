package liou.rayyuan.ebooksearchtaiwan.model.domain.repository

import liou.rayyuan.ebooksearchtaiwan.model.data.api.BookSearchService
import liou.rayyuan.ebooksearchtaiwan.model.data.BookStoreKeys
import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkCrawerResult
import liou.rayyuan.ebooksearchtaiwan.model.data.mapper.SearchResultMapper
import liou.rayyuan.ebooksearchtaiwan.model.domain.Result
import liou.rayyuan.ebooksearchtaiwan.model.domain.SimpleResult
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookStores

class BookRepositoryImpl(
    private val bookSearchService: BookSearchService,
    private val searchResultMapper: SearchResultMapper
) : BookRepository {

    override suspend fun getBooks(keyword: String): SimpleResult<BookStores> {
        return try {
            val response = bookSearchService.postBooks(keyword)
            Result.Success(mapBookStores(response))
        } catch (exception: Exception) {
            Result.Failed(BookResultException("Response is failed, exception is $exception", exception))
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

    class BookResultException(message: String, exception: Exception) : Throwable(message)
}