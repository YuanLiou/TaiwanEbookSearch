package liou.rayyuan.ebooksearchtaiwan.model.data.mapper

import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkBook
import liou.rayyuan.ebooksearchtaiwan.model.data.mapper.basic.ListMapper
import liou.rayyuan.ebooksearchtaiwan.model.data.mapper.basic.NullableInputListMapper
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.utils.DefaultStoreNames

class BookListMapper(
    private val bookMapper: BookDataMapper
) : NullableInputListMapper<NetworkBook, Book> {

    fun setupBookStore(storeName: String?) {
        if (storeName == null) {
            return
        }
        val store = DefaultStoreNames.fromName(storeName)
        bookMapper.setupBookStore(store)
    }

    override fun map(input: List<NetworkBook>?): List<Book> {
        if (input == null) {
            return emptyList()
        }
        return input.map { bookMapper.map(it) }
    }
}