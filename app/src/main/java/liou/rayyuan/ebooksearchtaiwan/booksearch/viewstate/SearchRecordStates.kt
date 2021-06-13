package liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate

sealed class SearchRecordStates {
    class ShowList(val itemCounts: Int): SearchRecordStates()
    object HideList: SearchRecordStates()
}