package com.rayliu.commonmain.domain.repository

import com.rayliu.commonmain.domain.SimpleResult
import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.data.DefaultStoreNames
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun getBooks(keyword: String): SimpleResult<BookStores>
    suspend fun getBooksWithStores(stores: List<DefaultStoreNames>, keyword: String): SimpleResult<BookStores>
    fun getDefaultResultSort(): Flow<List<DefaultStoreNames>>
    suspend fun saveDefaultResultSort(currentSortSettings: List<DefaultStoreNames>)
}