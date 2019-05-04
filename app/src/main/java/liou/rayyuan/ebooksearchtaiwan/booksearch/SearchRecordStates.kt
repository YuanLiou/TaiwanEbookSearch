package liou.rayyuan.ebooksearchtaiwan.booksearch

sealed class SearchRecordStates {
    class ShowList(val itemCounts: Int): SearchRecordStates()
    object HideList: SearchRecordStates()
}