package com.rayliu.commonmain.domain.repository

import com.rayliu.commonmain.UserPreferenceManager
import com.rayliu.commonmain.Utils
import com.rayliu.commonmain.data.api.BookSearchService
import com.rayliu.commonmain.data.dto.NetworkCrawerResult
import com.rayliu.commonmain.data.mapper.BookStoresMapper
import com.rayliu.commonmain.domain.Result
import com.rayliu.commonmain.domain.SimpleResult
import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.data.DefaultStoreNames

class BookRepositoryImpl(
    private val bookSearchService: BookSearchService,
    private val bookStoresMapper: BookStoresMapper,
    private val preferenceManager: UserPreferenceManager
) : BookRepository {

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

    override fun getDefaultResultSort(): List<DefaultStoreNames> {
        val userDefaultSort = preferenceManager.getBookStoreSort()
        if (userDefaultSort != null) {
            return userDefaultSort
        }

        val defaultSort = Utils.getDefaultSort()
        preferenceManager.saveBookStoreSort(defaultSort)
        return defaultSort
    }

    class BookResultException(message: String, exception: Exception) : Throwable(message)
}