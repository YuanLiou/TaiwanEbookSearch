package liou.rayyuan.ebooksearchtaiwan.bookstorereorder

import com.rayliu.commonmain.data.DefaultStoreNames
import kotlinx.collections.immutable.ImmutableList
import liou.rayyuan.ebooksearchtaiwan.arch.IState

sealed class BookStoreReorderViewState : IState {
    class PrepareBookSort(
        val bookSort: ImmutableList<DefaultStoreNames>
    ) : BookStoreReorderViewState()

    data object BackToPreviousPage : BookStoreReorderViewState()
}
