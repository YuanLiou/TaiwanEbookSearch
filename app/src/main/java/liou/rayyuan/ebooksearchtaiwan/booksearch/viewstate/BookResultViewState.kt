package liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate

import liou.rayyuan.ebooksearchtaiwan.arch.IState

/**
 * Created by louis383 on 2017/12/4.
 */
sealed class BookResultViewState : IState {
    data object PrepareBookResult : BookResultViewState()

    data class ShowBooks(
        val keyword: String
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
