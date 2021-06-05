package liou.rayyuan.ebooksearchtaiwan.model.data.mapper

import liou.rayyuan.ebooksearchtaiwan.model.data.BookStoreKeys
import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkCrawerResult
import liou.rayyuan.ebooksearchtaiwan.model.data.mapper.basic.Mapper
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookResult
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookStore
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookStores

class BookStoresMapper(
    private val searchResultMapper: SearchResultMapper
) : Mapper<NetworkCrawerResult, BookStores> {

    override fun map(input: NetworkCrawerResult): BookStores {
        val convertedResult = searchResultMapper.map(input)
        val books = convertedResult.bookStores.associateBy { it.bookStoreDetails?.id }

        return BookStores(
            booksCompany = books[BookStoreKeys.booksCompany]?.createBookResult(),
            readmoo = books[BookStoreKeys.readmoo]?.createBookResult(),
            kobo = books[BookStoreKeys.kobo]?.createBookResult(),
            taaze = books[BookStoreKeys.taaze]?.createBookResult(),
            bookWalker = books[BookStoreKeys.bookwalker]?.createBookResult(),
            playStore = books[BookStoreKeys.playStore]?.createBookResult(),
            pubu = books[BookStoreKeys.pubu]?.createBookResult(),
            hyread = books[BookStoreKeys.hyread]?.createBookResult(),
            kindle = books[BookStoreKeys.kindle]?.createBookResult()
        )
    }

    private fun BookStore.createBookResult(): BookResult {
        return BookResult(
            books = books,
            isOnline = bookStoreDetails?.isOnline ?: false,
            isOkay = isOkay,
            status = status
        )
    }
}