package liou.rayyuan.ebooksearchtaiwan.bookstorereorder

import com.rayliu.commonmain.data.DefaultStoreNames
import liou.rayyuan.ebooksearchtaiwan.arch.IUserIntent

sealed class BookStoreReorderUserIntent : IUserIntent {
    object GetPreviousSavedSort : BookStoreReorderUserIntent()

    data class UpdateSort(
        val bookSorts: List<DefaultStoreNames>
    ) : BookStoreReorderUserIntent()
}
