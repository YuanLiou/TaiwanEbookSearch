package com.rayliu.commonmain.domain.model

data class BookStore(
    val bookStoreDetails: BookStoreDetails?,
    val books: List<Book>,
    val isOkay: Boolean,
    val status: String,
    val total: Int
)
