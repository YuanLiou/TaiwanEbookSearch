package com.rayliu.commonmain.domain.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rayliu.commonmain.Utils
import com.rayliu.commonmain.data.api.BookSearchService
import com.rayliu.commonmain.data.dto.NetworkCrawerResult
import com.rayliu.commonmain.data.mapper.BookStoresMapper
import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.data.DefaultStoreNames
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class BookRepositoryImpl(
    private val bookSearchService: BookSearchService,
    private val bookStoresMapper: BookStoresMapper,
    private val userPreferences: DataStore<Preferences>
) : BookRepository {
    companion object {
        private const val KEY_BOOK_STORE_SORT = "key-book-store-sort"
    }

    override suspend fun getBooks(keyword: String): Result<BookStores> {
        return runCatching {
            mapBookStores(bookSearchService.postBooks(keyword))
        }
    }

    override suspend fun getBooksWithStores(
        stores: List<DefaultStoreNames>,
        keyword: String
    ): Result<BookStores> {
        val storeStringList = stores.map { it.defaultName }
        return runCatching {
            mapBookStores(bookSearchService.postBooks(storeStringList, keyword))
        }
    }

    private fun mapBookStores(input: NetworkCrawerResult): BookStores {
        return bookStoresMapper.map(input)
    }

    override fun getDefaultResultSort(): Flow<List<DefaultStoreNames>> {
        val key = stringPreferencesKey(KEY_BOOK_STORE_SORT)
        return userPreferences.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preference ->
                val userDefaultSort = preference[key] ?: ""
                if (userDefaultSort.isNotBlank()) {
                    val result = userDefaultSort.split(",").map {
                        DefaultStoreNames.fromName(it)
                    }
                    return@map result
                }

                val defaultSort = Utils.getDefaultSort()
                saveDefaultResultSort(defaultSort)
                defaultSort
            }
    }

    override suspend fun saveDefaultResultSort(currentSortSettings: List<DefaultStoreNames>) {
        val key = stringPreferencesKey(KEY_BOOK_STORE_SORT)
        userPreferences.edit { settings ->
            val settingString = currentSortSettings.joinToString(separator = ",") { it.defaultName }
            settings[key] = settingString
        }
    }

    override suspend fun getSearchSnapshot(searchId: String): Result<BookStores> {
        return runCatching {
            mapBookStores(bookSearchService.getSearchSnapshot(searchId))
        }
    }
}