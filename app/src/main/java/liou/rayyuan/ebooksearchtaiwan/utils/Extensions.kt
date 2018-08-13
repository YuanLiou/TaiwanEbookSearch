package liou.rayyuan.ebooksearchtaiwan.utils

import android.content.Context
import liou.rayyuan.ebooksearchtaiwan.R


fun DefaultStoreNames.getLocalizedName(context: Context): String = when (this) {
    DefaultStoreNames.BEST_RESULT -> context.getString(R.string.best_result_title)
    DefaultStoreNames.BOOK_COMPANY -> context.getString(R.string.books_company_title)
    DefaultStoreNames.READMOO -> context.getString(R.string.readmoo_title)
    DefaultStoreNames.KOBO -> context.getString(R.string.kobo_title)
    DefaultStoreNames.TAAZE -> context.getString(R.string.taaze_title)
    DefaultStoreNames.BOOK_WALKER -> context.getString(R.string.book_walker_title)
    DefaultStoreNames.PLAY_STORE -> context.getString(R.string.playbook_title)
    DefaultStoreNames.PUBU -> context.getString(R.string.pubu_title)
    DefaultStoreNames.HYREAD -> context.getString(R.string.hyread_title)
}