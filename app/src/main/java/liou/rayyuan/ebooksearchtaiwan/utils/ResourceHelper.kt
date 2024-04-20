package liou.rayyuan.ebooksearchtaiwan.utils

import android.content.Context
import androidx.annotation.StringRes

class ResourceHelper(
    private val context: Context
) {
    fun getString(
        @StringRes stringResId: Int,
        vararg formatArgs: Any
    ): String = context.resources.getString(stringResId, *formatArgs)
}
