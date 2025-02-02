package liou.rayyuan.ebooksearchtaiwan.bookstorereorder

sealed class BookStoreReorderViewState {
    data object BackToPreviousPage : BookStoreReorderViewState()
}
