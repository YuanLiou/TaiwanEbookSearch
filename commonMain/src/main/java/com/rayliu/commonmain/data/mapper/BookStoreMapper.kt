package com.rayliu.commonmain.data.mapper

import com.rayliu.commonmain.data.dto.NetworkResult
import com.rayliu.commonmain.data.mapper.basic.Mapper
import com.rayliu.commonmain.domain.model.BookStore

class BookStoreMapper(
    private val bookListMapper: BookListMapper,
    private val bookStoreDetailsMapper: BookStoreDetailsMapper
) : Mapper<NetworkResult, BookStore> {
    fun setKeywords(keywords: String) {
        bookListMapper.setupKeywords(keywords)
    }

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
