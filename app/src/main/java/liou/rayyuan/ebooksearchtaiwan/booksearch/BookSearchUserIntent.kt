package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.compose.ui.text.input.TextFieldValue
import com.google.android.play.core.review.ReviewInfo
import com.rayliu.commonmain.domain.model.SearchRecord
import liou.rayyuan.ebooksearchtaiwan.arch.IUserIntent

sealed class BookSearchUserIntent : IUserIntent {
    data class FocusOnTextEditing(
        val isFocus: Boolean
    ) : BookSearchUserIntent()

    data class SearchBook(
        val keywords: String? = null
    ) : BookSearchUserIntent()

    data class ShowSearchSnapshot(
        val searchId: String
    ) : BookSearchUserIntent()

    data class DeleteSearchRecord(
        val searchRecord: SearchRecord
    ) : BookSearchUserIntent()

    data class AskUserRankApp(
        val reviewInfo: ReviewInfo
    ) : BookSearchUserIntent()

    data class UpdateKeyword(
        val keywords: TextFieldValue
    ) : BookSearchUserIntent()

    data class UpdateTextInputFocusState(
        val isFocused: Boolean
    ) : BookSearchUserIntent()

    data class ForceFocusOrUnfocusKeywordTextInput(
        val focus: Boolean
    ) : BookSearchUserIntent()

    data class ForceShowOrHideVirtualKeyboard(
        val show: Boolean
    ) : BookSearchUserIntent()

    data object CopySnapshotUrlToClipboard : BookSearchUserIntent()

    data object OnViewReadyToServe : BookSearchUserIntent()

    data object ShareSnapshot : BookSearchUserIntent()

    data object RankAppWindowHasShown : BookSearchUserIntent()

    data object CheckServiceStatus : BookSearchUserIntent()

    data object ResetFocusAction : BookSearchUserIntent()

    data object ResetVirtualKeyboardAction : BookSearchUserIntent()
}
