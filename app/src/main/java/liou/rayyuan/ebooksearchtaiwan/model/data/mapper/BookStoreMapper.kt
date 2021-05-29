package liou.rayyuan.ebooksearchtaiwan.model.data.mapper

import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkResult
import liou.rayyuan.ebooksearchtaiwan.model.data.mapper.basic.Mapper
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookStore

class BookStoreMapper(
    val bookListMapper: BookListMapper,
    val bookStoreDetailsMapper: BookStoreDetailsMapper
) : Mapper<NetworkResult, BookStore> {
    override fun map(input: NetworkResult): BookStore {
        val bookStoreId = input.bookstore.id
        bookListMapper.setupBookStore(bookStoreId)
        return BookStore(
            bookStoreDetails = bookStoreDetailsMapper.map(input.bookstore),
            books = bookListMapper.map(input.books),
            isOkay = input.isOkay ?: false,
            status = input.status ?: "",
            total = input.quantity ?: 0
        )
    }
}