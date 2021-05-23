package liou.rayyuan.ebooksearchtaiwan.model

import liou.rayyuan.ebooksearchtaiwan.model.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookStores
import liou.rayyuan.ebooksearchtaiwan.utils.DefaultStoreNames


fun BookStores.generateBookStoresResultMap(sortedList: List<DefaultStoreNames>): Map<DefaultStoreNames, List<Book>> {
    val map = mutableMapOf<DefaultStoreNames, List<Book>>()

    for (store in sortedList) {
        when (store) {
            DefaultStoreNames.BOOK_COMPANY -> {
                this.booksCompany?.let {
                    map.put(DefaultStoreNames.BOOK_COMPANY, it)
                }
            }
            DefaultStoreNames.READMOO -> {
                this.readmoo?.let {
                    map.put(DefaultStoreNames.READMOO, it)
                }
            }
            DefaultStoreNames.KOBO -> {
                this.kobo?.let {
                    map.put(DefaultStoreNames.KOBO, it)
                }
            }
            DefaultStoreNames.TAAZE -> {
                this.taaze?.let {
                    map.put(DefaultStoreNames.TAAZE, it)
                }
            }
            DefaultStoreNames.BOOK_WALKER -> {
                this.bookWalker?.let {
                    map.put(DefaultStoreNames.BOOK_WALKER, it)
                }
            }
            DefaultStoreNames.PLAY_STORE -> {
                this.playStore?.let {
                    map.put(DefaultStoreNames.PLAY_STORE, it)
                }
            }
            DefaultStoreNames.PUBU -> {
                this.pubu?.let {
                    map.put(DefaultStoreNames.PUBU, it)
                }
            }
            DefaultStoreNames.HYREAD -> {
                this.hyread?.let {
                    map.put(DefaultStoreNames.HYREAD, it)
                }
            }
            DefaultStoreNames.KINDLE -> {
                this.kindle?.let {
                    map.put(DefaultStoreNames.KINDLE, it)
                }
            }
            else -> {}
        }
    }
    return map
}
