package liou.rayyuan.ebooksearchtaiwan.model.domain.repository

import liou.rayyuan.ebooksearchtaiwan.model.domain.SimpleResult
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookStores

interface BookRepository {
    suspend fun getBooks(keyword: String): SimpleResult<BookStores>
}