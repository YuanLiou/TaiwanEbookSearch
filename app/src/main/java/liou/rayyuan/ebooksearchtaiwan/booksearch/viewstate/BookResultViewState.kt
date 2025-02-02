package liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate

/**
 * Created by louis383 on 2017/12/4.
 */
sealed class BookResultViewState {
    data object PrepareBookResult : BookResultViewState()

    data class ShowBooks(
        val keyword: String
    ) : BookResultViewState()

    data object PrepareBookResultError : BookResultViewState()

    data class ShareCurrentPageSnapshot(
        val url: String
    ) : BookResultViewState()
}
