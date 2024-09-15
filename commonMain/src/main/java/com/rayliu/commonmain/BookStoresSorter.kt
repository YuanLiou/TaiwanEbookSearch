package com.rayliu.commonmain

import com.rayliu.commonmain.domain.model.BookResult
import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.data.DefaultStoreNames

object BookStoresSorter {
    fun generateResultMap(
        bookStores: BookStores,
        sortedList: List<DefaultStoreNames>
    ): Map<DefaultStoreNames, BookResult> {
        val map = mutableMapOf<DefaultStoreNames, BookResult>()

        for (store in sortedList) {
            when (store) {
                DefaultStoreNames.BOOK_COMPANY -> {
                    bookStores.booksCompany?.let {
                        map.put(DefaultStoreNames.BOOK_COMPANY, it)
                    }
                }

                DefaultStoreNames.READMOO -> {
                    bookStores.readmoo?.let {
                        map.put(DefaultStoreNames.READMOO, it)
                    }
                }

                DefaultStoreNames.KOBO -> {
                    bookStores.kobo?.let {
                        map.put(DefaultStoreNames.KOBO, it)
                    }
                }

                DefaultStoreNames.TAAZE -> {
                    bookStores.taaze?.let {
                        map.put(DefaultStoreNames.TAAZE, it)
                    }
                }

                DefaultStoreNames.BOOK_WALKER -> {
                    bookStores.bookWalker?.let {
                        map.put(DefaultStoreNames.BOOK_WALKER, it)
                    }
                }

                DefaultStoreNames.PLAY_STORE -> {
                    bookStores.playStore?.let {
                        map.put(DefaultStoreNames.PLAY_STORE, it)
                    }
                }

                DefaultStoreNames.PUBU -> {
                    bookStores.pubu?.let {
                        map.put(DefaultStoreNames.PUBU, it)
                    }
                }

                DefaultStoreNames.HYREAD -> {
                    bookStores.hyread?.let {
                        map.put(DefaultStoreNames.HYREAD, it)
                    }
                }

                DefaultStoreNames.KINDLE -> {
                    bookStores.kindle?.let {
                        map.put(DefaultStoreNames.KINDLE, it)
                    }
                }

                DefaultStoreNames.LIKERLAND -> {
                    bookStores.likerLand?.let {
                        map.put(DefaultStoreNames.LIKERLAND, it)
                    }
                }

                else -> {}
            }
        }
        return map
    }
}
