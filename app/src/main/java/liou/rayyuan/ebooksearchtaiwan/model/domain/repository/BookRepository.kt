package liou.rayyuan.ebooksearchtaiwan.model.domain.repository

import liou.rayyuan.ebooksearchtaiwan.model.domain.SimpleResult
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookStores
import liou.rayyuan.ebooksearchtaiwan.utils.DefaultStoreNames

interface BookRepository {
    suspend fun getBooks(keyword: String): SimpleResult<BookStores>
    suspend fun getBooksWithStores(stores: List<DefaultStoreNames>, keyword: String): SimpleResult<BookStores>
}