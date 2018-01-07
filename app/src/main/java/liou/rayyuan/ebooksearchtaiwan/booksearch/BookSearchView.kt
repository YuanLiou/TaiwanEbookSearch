package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.net.Uri
import liou.rayyuan.ebooksearchtaiwan.view.ViewState

/**
 * Created by louis383 on 2017/12/2.
 */

interface BookSearchView {
    fun setupInterface()
    fun setMainResultView(viewState: ViewState)
    fun scrollToTop()
    fun openBookLink(uri: Uri)
    fun isInternetConnectionAvailable(): Boolean
    fun showInternetRequestDialog()
    fun showInternetConnectionTimeout()
    fun showKeywordIsEmpty()
    fun hideVirtualKeyboard()
    fun showVirtualKeyboard()
    fun showEasterEgg01()
    fun showErrorMessage(message: String)
    fun getApplicationString(stringId: Int): String
}
