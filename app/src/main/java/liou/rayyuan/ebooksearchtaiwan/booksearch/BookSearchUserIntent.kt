package liou.rayyuan.ebooksearchtaiwan.booksearch

import com.google.android.play.core.review.ReviewInfo
import liou.rayyuan.ebooksearchtaiwan.arch.IUserIntent

sealed class BookSearchUserIntent : IUserIntent {
    data class ShowSearchSnapshot(
        val searchId: String
    ) : BookSearchUserIntent()

    data class AskUserRankApp(
        val reviewInfo: ReviewInfo
    ) : BookSearchUserIntent()

    data class EnableCameraButtonClick(
        val enable: Boolean
    ) : BookSearchUserIntent()

    data class EnableSearchButtonClick(
        val enable: Boolean
    ) : BookSearchUserIntent()

    data class ShowCopyUrlOption(
        val show: Boolean
    ) : BookSearchUserIntent()

    data class ShowShareSnapshotOption(
        val show: Boolean
    ) : BookSearchUserIntent()

    data object OnViewReadyToServe : BookSearchUserIntent()

    data object RankAppWindowHasShown : BookSearchUserIntent()

    data object CheckServiceStatus : BookSearchUserIntent()

    data object ResetVirtualKeyboardAction : BookSearchUserIntent()
}
