package com.rayliu.commonmain.domain.model

data class BookStores(
    val searchId: String,
    val searchKeyword: String,
    val booksCompany: BookResult?,
    val readmoo: BookResult?,
    val kobo: BookResult?,
    val taaze: BookResult?,
    val bookWalker: BookResult?,
    val playStore: BookResult?,
    val pubu: BookResult?,
    val hyread: BookResult?,
    val kindle: BookResult?,
    val likerLand: BookResult?
)
