package liou.rayyuan.ebooksearchtaiwan.booksearch

import com.rayliu.commonmain.domain.model.SearchRecord
import liou.rayyuan.ebooksearchtaiwan.arch.IUserIntent

sealed class BookSearchUserIntent : IUserIntent {
    data class FocusOnTextEditing(val isFocus: Boolean) : BookSearchUserIntent()
    data class SearchBook(val keywords: String) : BookSearchUserIntent()
    data class DeleteSearchRecord(val searchRecord: SearchRecord) : BookSearchUserIntent()
    object OnViewReadyToServe : BookSearchUserIntent()
    object PressHint : BookSearchUserIntent()
}