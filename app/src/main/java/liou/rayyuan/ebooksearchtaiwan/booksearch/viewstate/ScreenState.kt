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

    data object ConnectionTimeout : ScreenState()

    data object NetworkError : ScreenState()

    data object EmptyKeyword : ScreenState()

    data object NoInternetConnection : ScreenState()

    data object NoSharingContentAvailable : ScreenState()
}
