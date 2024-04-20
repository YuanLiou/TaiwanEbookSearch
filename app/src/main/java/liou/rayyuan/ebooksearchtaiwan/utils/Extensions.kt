package liou.rayyuan.ebooksearchtaiwan.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes

fun String?.showToastOn(
    context: Context,
    duration: Int = Toast.LENGTH_LONG
): Toast {
    val message = this ?: ""
    return Toast.makeText(context, message, duration).apply { show() }
}

fun <T : View> Activity.bindView(
    @IdRes resId: Int
): Lazy<T> = lazy { findViewById<T>(resId) }

/***
 * Guard Let: Use this function to check all parameters is not null.
 *
 * If any of the passed value is null, this function will perform abortAction.
 *
 * It's recommend to use this function to do **early return**.
 */
inline fun <T : Any> guardLet(
    vararg elements: T?,
    abortAction: () -> Nothing
): List<T> {
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
inline fun <T : Any> ifLet(
    vararg elements: T?,
    action: (List<T>) -> Unit
) {
    val isAllElementsAvailable = elements.all { it != null }
    if (isAllElementsAvailable) {
        action(elements.filterNotNull())
    }
}
