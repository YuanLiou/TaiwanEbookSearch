package com.rayliu.commonmain.domain.search

import com.rayliu.commonmain.BookStoresSorter
import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.model.BookSearchSlice
import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.domain.model.StoreResultSection

object BookStoresResultSlicer {
    const val MAX_STORE_BOOKS = 10

    fun slice(
        bookStores: BookStores,
        enabledStores: List<DefaultStoreNames>,
        sortStoreBooksByPrice: Boolean
    ): BookSearchSlice {
        val groupedResults = BookStoresSorter.generateResultMap(bookStores, enabledStores)
        val bestResults =
            groupedResults
                .mapNotNull { (store, bookResult) ->
                    if (!enabledStores.contains(store)) {
                        null
                    } else {
                        bookResult.books.firstOrNull()?.copy(isFirstChoice = true)
                    }
                }.sortedBy { it.price }

        val storeSections =
            enabledStores.mapNotNull { storeName ->
                val bookResult = groupedResults[storeName] ?: return@mapNotNull null
                val books =
                    bookResult.books
                        .drop(1)
                        .take(MAX_STORE_BOOKS)
                        .let {
                            if (sortStoreBooksByPrice) {
                                it.sortedBy { book -> book.price }
                            } else {
                                it.sortedByDescending { book -> book.titleKeywordSimilarity }
                            }
                        }

                StoreResultSection(
                    store = storeName,
                    isOnline = bookResult.isOnline,
                    isOkay = bookResult.isOkay,
                    status = bookResult.status,
                    books = books
                )
            }

        return BookSearchSlice(
            bestResults = bestResults,
            storeSections = storeSections
        )
    }
}
