package liou.rayyuan.ebooksearchtaiwan.utils

import android.content.Context
import androidx.annotation.StringRes

class ResourceHelper(private val context: Context) {

    fun getString(@StringRes stringResId: Int): String {
        return context.resources.getString(stringResId)
    }

    fun getString(@StringRes stringResId: Int, vararg formatArgs: Any): String {
        return context.resources.getString(stringResId, *formatArgs)
    }
}
