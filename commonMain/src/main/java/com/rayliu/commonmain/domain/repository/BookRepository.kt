package com.rayliu.commonmain.domain.repository

import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.data.DefaultStoreNames
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun getBooks(keyword: String): Result<BookStores>
    suspend fun getBooksWithStores(stores: List<DefaultStoreNames>, keyword: String): Result<BookStores>
    fun getDefaultResultSort(): Flow<List<DefaultStoreNames>>
    suspend fun saveDefaultResultSort(currentSortSettings: List<DefaultStoreNames>)
    suspend fun getSearchSnapshot(searchId: String): Result<BookStores>
}