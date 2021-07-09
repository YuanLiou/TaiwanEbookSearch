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
import com.rayliu.commonmain.domain.Result
import com.rayliu.commonmain.domain.SimpleResult
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

    override suspend fun getBooks(keyword: String): SimpleResult<BookStores> {
        return try {
            val response = bookSearchService.postBooks(keyword)
            Result.Success(mapBookStores(response))
        } catch (exception: Exception) {
            Result.Failed(BookResultException("Response is failed, exception is $exception", exception))
        }
    }

    override suspend fun getBooksWithStores(
        stores: List<DefaultStoreNames>,
        keyword: String
    ): SimpleResult<BookStores> {
        val storeStringList = stores.map { it.defaultName }
        return try {
            val response = bookSearchService.postBooks(storeStringList, keyword)
            Result.Success(mapBookStores(response))
        } catch (exception: Exception) {
            Result.Failed(BookResultException("Response is failed, exception is $exception", exception))
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
                    emptyPreferences()
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

    class BookResultException(message: String, exception: Exception) : Throwable(message)
}