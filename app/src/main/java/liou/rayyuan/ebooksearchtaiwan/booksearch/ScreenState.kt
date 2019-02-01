package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.annotation.StringRes

internal sealed class ScreenState {
    object EasterEgg : ScreenState()
    object ConnectionTimeout : ScreenState()
    object NetworkError : ScreenState()
    object EmptyKeyword : ScreenState()
    object NoInternetConnection : ScreenState()
    class ShowToastMessage(@StringRes val stringResId: Int = -1, val message: String) : ScreenState()
}