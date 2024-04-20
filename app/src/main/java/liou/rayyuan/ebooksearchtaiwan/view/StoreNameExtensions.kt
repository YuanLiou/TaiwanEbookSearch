package liou.rayyuan.ebooksearchtaiwan.view

import android.content.Context
import androidx.annotation.StringRes
import liou.rayyuan.ebooksearchtaiwan.R
import com.rayliu.commonmain.data.DefaultStoreNames

@StringRes
fun DefaultStoreNames.getStringResource(): Int =
    when (this) {
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

fun DefaultStoreNames.getLocalizedName(context: Context): String =
    when (this) {
        DefaultStoreNames.BEST_RESULT -> context.getString(R.string.best_result_title)
        DefaultStoreNames.BOOK_COMPANY -> context.getString(R.string.books_company_title)
        DefaultStoreNames.READMOO -> context.getString(R.string.readmoo_title)
        DefaultStoreNames.KOBO -> context.getString(R.string.kobo_title)
        DefaultStoreNames.TAAZE -> context.getString(R.string.taaze_title)
        DefaultStoreNames.BOOK_WALKER -> context.getString(R.string.book_walker_title)
        DefaultStoreNames.PLAY_STORE -> context.getString(R.string.playbook_title)
        DefaultStoreNames.PUBU -> context.getString(R.string.pubu_title)
        DefaultStoreNames.HYREAD -> context.getString(R.string.hyread_title)
        DefaultStoreNames.KINDLE -> context.getString(R.string.kindle_title)
        DefaultStoreNames.UNKNOWN -> context.getString(R.string.book_source_unknown)
    }
