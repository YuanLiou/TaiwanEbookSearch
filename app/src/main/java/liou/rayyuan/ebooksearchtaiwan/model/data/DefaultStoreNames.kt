package liou.rayyuan.ebooksearchtaiwan.model.data

import androidx.annotation.StringRes
import liou.rayyuan.ebooksearchtaiwan.R

enum class DefaultStoreNames(val defaultName: String) {
    BEST_RESULT("BestResult"),
    BOOK_COMPANY(BookStoreKeys.booksCompany),
    KINDLE(BookStoreKeys.kindle),
    READMOO(BookStoreKeys.readmoo),
    KOBO(BookStoreKeys.kobo),
    TAAZE(BookStoreKeys.kobo),
    BOOK_WALKER(BookStoreKeys.bookwalker),
    PLAY_STORE(BookStoreKeys.playStore),
    PUBU(BookStoreKeys.pubu),
    HYREAD(BookStoreKeys.hyread),
    UNKNOWN("unknown");

    companion object {
        private val map = values().associateBy { it.defaultName }
        fun fromName(storeName: String): DefaultStoreNames = map[storeName] ?: UNKNOWN
    }

    fun isSame(passedValue: DefaultStoreNames): Boolean {
        return defaultName == passedValue.defaultName
    }
}

