package liou.rayyuan.ebooksearchtaiwan.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

fun String?.showToastOn(
    context: Context,
    duration: Int = Toast.LENGTH_LONG
): Toast {
    val message = this ?: ""
    return Toast.makeText(context, message, duration).apply { show() }
}

fun <T : View> Activity.bindView(
    @IdRes resId: Int
): Lazy<T> = lazy { findViewById(resId) }

fun <T : View> T.setupEdgeToEdge(customizeInsets: ((View, WindowInsetsCompat) -> Unit)? = null) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        if (customizeInsets != null) {
            customizeInsets(view, insets)
        } else {
            val bars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
                )

            view.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right,
                bottom = bars.bottom
            )
        }

        WindowInsetsCompat.CONSUMED
    }
}

fun <T : View> T.updateMargins(
    start: Int = 0,
    top: Int = 0,
    end: Int = 0,
    bottom: Int = 0
) {
    val marginLayoutParams = layoutParams as ViewGroup.MarginLayoutParams
    marginLayoutParams.setMargins(
        start,
        top,
        end,
        bottom
    )
    layoutParams = marginLayoutParams
}
