package liou.rayyuan.ebooksearchtaiwan.bookstorereorder

import com.rayliu.commonmain.data.DefaultStoreNames
import liou.rayyuan.ebooksearchtaiwan.arch.IState

sealed class BookStoreReorderViewState : IState {
    class PrepareBookSort(val bookSort: List<DefaultStoreNames>) : BookStoreReorderViewState()
    object BackToPreviousPage : BookStoreReorderViewState()
}
