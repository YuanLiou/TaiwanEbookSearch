package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import liou.rayyuan.ebooksearchtaiwan.view.ViewState

/**
 * Created by louis383 on 2017/12/2.
 */

interface BookSearchView {
    fun setupUI()
    fun setMainResultView(viewState: ViewState)
    fun scrollToTop()
    fun openBookLink(uri: Uri)
    fun isInternetConnectionAvailable(): Boolean
    fun showInternetRequestDialog()
    fun showInternetConnectionTimeout()
    fun showKeywordIsEmpty()
    fun hideVirtualKeyboard()
    fun focusBookSearchEditText()
    fun showEasterEgg01()
    fun showErrorMessage(message: String)
    fun showNetworkErrorMessage()
    fun getViewModelProvider(): ViewModelProvider
    fun getLifeCycleOwner(): LifecycleOwner
    fun backToListTop()
    fun showToast(message: String)
}
