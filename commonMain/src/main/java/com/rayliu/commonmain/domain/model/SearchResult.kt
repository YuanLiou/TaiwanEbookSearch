package com.rayliu.commonmain.domain.model

data class SearchResult(
    val keyword: String,
    val apiVersion: String,
    val bookStores: List<BookStore>,
    val totalBookCounts: Int
)
