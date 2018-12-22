package liou.rayyuan.ebooksearchtaiwan.utils

import android.content.Context
import liou.rayyuan.ebooksearchtaiwan.R

class QuickChecker(context: Context) {

    private val context = context.applicationContext

    fun isTabletSize(): Boolean = context.resources.getBoolean(R.bool.isTabletSize)

}