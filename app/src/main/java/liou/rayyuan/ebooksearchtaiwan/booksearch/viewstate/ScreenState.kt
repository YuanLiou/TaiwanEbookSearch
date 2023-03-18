package liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate

import androidx.annotation.StringRes

sealed class ScreenState {
    object EasterEgg : ScreenState()
    object ConnectionTimeout : ScreenState()
    object NetworkError : ScreenState()
    object EmptyKeyword : ScreenState()
    object NoInternetConnection : ScreenState()
    object NoSharingContentAvailable : ScreenState()
    object ShowUserRankingDialog : ScreenState()
    class ShowToastMessage(@StringRes val stringResId: Int = -1, val message: String) : ScreenState()
}