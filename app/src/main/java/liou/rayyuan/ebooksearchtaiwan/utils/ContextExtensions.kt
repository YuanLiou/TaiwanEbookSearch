package liou.rayyuan.ebooksearchtaiwan.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context?.showToastMessage(
    @StringRes stringRes: Int
) {
    if (this != null) {
        Toast.makeText(this, stringRes, Toast.LENGTH_LONG).show()
    }
}
