package com.rayliu.commonmain.domain.repository

import com.rayliu.commonmain.domain.model.BookStoreDetails
import kotlinx.collections.immutable.ImmutableList

interface BookStoreDetailsRepository {
    suspend fun getBookStoresDetail(): Result<ImmutableList<BookStoreDetails>>
}
