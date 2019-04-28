package liou.rayyuan.ebooksearchtaiwan.booksearch

sealed class SearchRecordStates {
    object ShowList: SearchRecordStates()
    object HideList: SearchRecordStates()
}