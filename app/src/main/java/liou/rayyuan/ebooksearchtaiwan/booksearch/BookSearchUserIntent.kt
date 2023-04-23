package liou.rayyuan.ebooksearchtaiwan.booksearch

import com.google.android.play.core.review.ReviewInfo
import com.rayliu.commonmain.domain.model.SearchRecord
import liou.rayyuan.ebooksearchtaiwan.arch.IUserIntent

sealed class BookSearchUserIntent : IUserIntent {
    data class FocusOnTextEditing(val isFocus: Boolean) : BookSearchUserIntent()
    data class SearchBook(val keywords: String) : BookSearchUserIntent()
    data class ShowSearchSnapshot(val searchId: String) : BookSearchUserIntent()
    data class DeleteSearchRecord(val searchRecord: SearchRecord) : BookSearchUserIntent()
    data class AskUserRankApp(val reviewInfo: ReviewInfo) : BookSearchUserIntent()
    object CopySnapshotUrlToClipboard : BookSearchUserIntent()
    object OnViewReadyToServe : BookSearchUserIntent()
    object PressHint : BookSearchUserIntent()
    object ShareSnapshot: BookSearchUserIntent()
    object RankAppWindowHasShown : BookSearchUserIntent()
}