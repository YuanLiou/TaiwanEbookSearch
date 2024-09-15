package com.rayliu.commonmain.domain.repository

import com.rayliu.commonmain.domain.model.BookStore

interface BookStoresRepository {
    suspend fun getBookStores(): Result<List<BookStore>>
}
