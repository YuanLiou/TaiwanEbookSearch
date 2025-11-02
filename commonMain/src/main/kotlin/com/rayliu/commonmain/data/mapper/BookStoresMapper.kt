package com.rayliu.commonmain.data.mapper

import com.rayliu.commonmain.data.BookStoreKeys
import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.data.dto.NetworkCrawerResult
import com.rayliu.commonmain.data.mapper.basic.Mapper
import com.rayliu.commonmain.domain.model.BookResult
import com.rayliu.commonmain.domain.model.BookStore
import com.rayliu.commonmain.domain.model.BookStores

class BookStoresMapper(
    private val searchResultMapper: SearchResultMapper
) : Mapper<NetworkCrawerResult, BookStores> {
    fun setEnableStores(enableStores: List<DefaultStoreNames>) {
        searchResultMapper.setEnableStores(enableStores)
    }

    override fun map(input: NetworkCrawerResult): BookStores {
        val convertedResult = searchResultMapper.map(input)
        val books = convertedResult.bookStores.associateBy { it.bookStoreDetails?.id }

        return BookStores(
            searchId = input.id.orEmpty(),
            searchKeyword = input.keywords.orEmpty(),
            booksCompany = books[BookStoreKeys.booksCompany]?.createBookResult(),
            readmoo = books[BookStoreKeys.readmoo]?.createBookResult(),
            kobo = books[BookStoreKeys.kobo]?.createBookResult(),
            taaze = books[BookStoreKeys.taaze]?.createBookResult(),
            bookWalker = books[BookStoreKeys.bookwalker]?.createBookResult(),
            playStore = books[BookStoreKeys.playStore]?.createBookResult(),
            pubu = books[BookStoreKeys.pubu]?.createBookResult(),
            hyread = books[BookStoreKeys.hyread]?.createBookResult(),
            kindle = books[BookStoreKeys.kindle]?.createBookResult(),
            likerLand = books[BookStoreKeys.likerLand]?.createBookResult()
        )
    }

    private fun BookStore.createBookResult(): BookResult =
        BookResult(
            books = books,
            isOnline = bookStoreDetails?.isOnline ?: false,
            isOkay = isOkay,
            status = status
        )
}
