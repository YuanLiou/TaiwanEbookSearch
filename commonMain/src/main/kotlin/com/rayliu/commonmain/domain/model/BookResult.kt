package com.rayliu.commonmain.domain.model

data class BookResult(
    val books: List<Book>,
    val isOnline: Boolean,
    val isOkay: Boolean,
    val status: String = ""
)
