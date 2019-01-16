package liou.rayyuan.ebooksearchtaiwan.utils

import androidx.annotation.StringRes
import liou.rayyuan.ebooksearchtaiwan.R

enum class DefaultStoreNames(val defaultName: String, @StringRes val defaultResId: Int) {
    BEST_RESULT("BestResult", R.string.best_result_title),

    BOOK_COMPANY("booksCompany", R.string.books_company_title),
    READMOO("readmoo", R.string.readmoo_title),
    KOBO("kobo", R.string.kobo_title),
    TAAZE("taaze", R.string.taaze_title),
    BOOK_WALKER("bookWalker", R.string.book_walker_title),
    PLAY_STORE("playStore", R.string.playbook_title),
    PUBU("pubu", R.string.pubu_title),
    HYREAD("hyread", R.string.hyread_title);

    companion object {
        private val map = DefaultStoreNames.values().associateBy { it.defaultName }
        fun fromName(storeName: String): DefaultStoreNames? = map[storeName]
    }
}

