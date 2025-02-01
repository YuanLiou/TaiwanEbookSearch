package liou.rayyuan.ebooksearchtaiwan.bookstorereorder.model

import androidx.compose.runtime.mutableStateOf
import com.rayliu.commonmain.data.DefaultStoreNames

data class SortedStore(
    val defaultStoreName: DefaultStoreNames,
    val isVisible: Boolean
) {
    var isEnable = mutableStateOf(isVisible)
}
