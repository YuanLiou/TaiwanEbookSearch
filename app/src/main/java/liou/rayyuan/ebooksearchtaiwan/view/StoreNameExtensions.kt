package liou.rayyuan.ebooksearchtaiwan.view

import androidx.annotation.StringRes
import liou.rayyuan.ebooksearchtaiwan.R
import com.rayliu.commonmain.data.DefaultStoreNames

@StringRes
fun DefaultStoreNames.getStringResource(): Int {
    return when (this) {
        DefaultStoreNames.BEST_RESULT -> R.string.best_result_title
        DefaultStoreNames.BOOK_COMPANY -> R.string.books_company_title
        DefaultStoreNames.KINDLE -> R.string.kindle_title
        DefaultStoreNames.READMOO -> R.string.readmoo_title
        DefaultStoreNames.KOBO -> R.string.kobo_title
        DefaultStoreNames.TAAZE -> R.string.taaze_title
        DefaultStoreNames.BOOK_WALKER -> R.string.book_walker_title
        DefaultStoreNames.PLAY_STORE -> R.string.playbook_title
        DefaultStoreNames.PUBU -> R.string.pubu_title
        DefaultStoreNames.HYREAD -> R.string.hyread_title
        DefaultStoreNames.UNKNOWN -> R.string.book_source_unknown
    }
}