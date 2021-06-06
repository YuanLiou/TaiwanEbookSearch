package liou.rayyuan.ebooksearchtaiwan.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes
import liou.rayyuan.ebooksearchtaiwan.R
import com.rayliu.commonmain.data.DefaultStoreNames


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
    DefaultStoreNames.KINDLE -> context.getString(R.string.kindle_title)
    DefaultStoreNames.UNKNOWN -> context.getString(R.string.book_source_unknown)
}

fun String?.showToastOn(context: Context, duration: Int = Toast.LENGTH_LONG): Toast {
    val message = this ?: ""
    return Toast.makeText(context, message, duration).apply { show() }
}

fun <T: View> Activity.bindView(@IdRes resId: Int): Lazy<T> =
        lazy { findViewById<T>(resId) }

/***
 * Guard Let: Use this function to check all parameters is not null.
 *
 * If any of the passed value is null, this function will perform abortAction.
 *
 * It's recommend to use this function to do **early return**.
 */
inline fun <T : Any> guardLet(vararg elements: T?, abortAction: () -> Nothing): List<T> {
    val isAllElementsAvailable = elements.all { it != null }
    return if (isAllElementsAvailable) {
        elements.filterNotNull()
    } else {
        abortAction()
    }
}

/***
 * If Let: Use this function to guarantee all parameters aren't null and do the task.
 *
 * The passed action will be performed while all parameters are not null.
 */
inline fun <T : Any> ifLet(vararg elements: T?, action: (List<T>) -> Unit) {
    val isAllElementsAvailable = elements.all { it != null }
    if (isAllElementsAvailable) {
        action(elements.filterNotNull())
    }
}
