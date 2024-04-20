package com.rayliu.commonmain.data.mapper

import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.data.dto.NetworkBook
import com.rayliu.commonmain.data.mapper.basic.NullableInputListMapper
import com.rayliu.commonmain.domain.model.Book

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

    fun setupKeywords(keywords: String) {
        bookMapper.setupKeywords(keywords)
    }

    override fun map(input: List<NetworkBook>?): List<Book> {
        if (input == null) {
            return emptyList()
        }
        return input.map { bookMapper.map(it) }
    }
}
