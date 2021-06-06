package com.rayliu.commonmain.data.mapper

import com.rayliu.commonmain.data.dto.NetworkBook
import com.rayliu.commonmain.data.mapper.basic.Mapper
import com.rayliu.commonmain.domain.model.Book
import com.rayliu.commonmain.data.DefaultStoreNames

class BookDataMapper : Mapper<NetworkBook, Book> {

    private var currentBookStore: DefaultStoreNames = DefaultStoreNames.UNKNOWN

    fun setupBookStore(store: DefaultStoreNames) {
        currentBookStore = store
    }

    override fun map(input: NetworkBook): Book {
        return with(input) {
            Book(
                thumbnail = thumbnail ?: "",
                priceCurrency = priceCurrency ?: "TWD",
                price = price ?: 0.0f,
                link = link ?: "",
                about = about ?: "",
                id = id ?: "",
                title = title ?: "",
                authors = authors.orEmpty(),
                bookStore = currentBookStore
            )
        }
    }
}