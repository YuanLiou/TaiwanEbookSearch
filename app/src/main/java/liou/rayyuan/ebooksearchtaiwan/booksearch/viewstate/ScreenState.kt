package liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate

import androidx.annotation.StringRes
import com.google.android.play.core.review.ReviewInfo

sealed class ScreenState {
    data class ShowUserRankingDialog(
        val reviewInfo: ReviewInfo
    ) : ScreenState()

    data class ShowToastMessage(
        @StringRes val stringResId: Int = -1,
        val message: String? = null
    ) : ScreenState()

    object EasterEgg : ScreenState()

    object ConnectionTimeout : ScreenState()

    object NetworkError : ScreenState()

    object EmptyKeyword : ScreenState()

    object NoInternetConnection : ScreenState()

    object NoSharingContentAvailable : ScreenState()
}
