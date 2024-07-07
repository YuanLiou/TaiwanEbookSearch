package liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate

import liou.rayyuan.ebooksearchtaiwan.arch.IState
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.AdapterItem

/**
 * Created by louis383 on 2017/12/4.
 */
sealed class BookResultViewState : IState {
    class PrepareBookResult(
        val scrollToTop: Boolean = false
    ) : BookResultViewState()

    data class ShowBooks(
        val keyword: String,
        val scrollPosition: Int,
        val adapterItems: List<AdapterItem>
    ) : BookResultViewState()

    data object PrepareBookResultError : BookResultViewState()

    data class ShareCurrentPageSnapshot(
        val url: String
    ) : BookResultViewState()

    data class ShowSearchRecordList(
        val itemCounts: Int
    ) : BookResultViewState()

    data object HideSearchRecordList : BookResultViewState()
}
