package com.rayliu.commonmain.domain.repository

import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.data.DefaultStoreNames
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun getBooks(keyword: String): Result<BookStores>

    suspend fun getBooksWithStores(
        stores: List<DefaultStoreNames>,
        keyword: String
    ): Result<BookStores>

    fun getDefaultResultSort(): Flow<ImmutableList<DefaultStoreNames>>

    suspend fun saveDefaultResultSort(currentSortSettings: ImmutableList<DefaultStoreNames>)

    suspend fun getSearchSnapshot(searchId: String): Result<BookStores>
}
