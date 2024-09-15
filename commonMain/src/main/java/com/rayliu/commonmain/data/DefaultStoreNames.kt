package com.rayliu.commonmain.data

enum class DefaultStoreNames(
    val defaultName: String
) {
    BEST_RESULT("BestResult"),
    BOOK_COMPANY(BookStoreKeys.booksCompany),
    KINDLE(BookStoreKeys.kindle),
    READMOO(BookStoreKeys.readmoo),
    KOBO(BookStoreKeys.kobo),
    TAAZE(BookStoreKeys.taaze),
    BOOK_WALKER(BookStoreKeys.bookwalker),
    PLAY_STORE(BookStoreKeys.playStore),
    PUBU(BookStoreKeys.pubu),
    HYREAD(BookStoreKeys.hyread),
    LIKERLAND(BookStoreKeys.likerLand),
    UNKNOWN("unknown");

    companion object {
        private val map = values().associateBy { it.defaultName }

        fun fromName(storeName: String): DefaultStoreNames = map[storeName] ?: UNKNOWN
    }

    fun isSame(passedValue: DefaultStoreNames): Boolean = defaultName == passedValue.defaultName
}
