package liou.rayyuan.ebooksearchtaiwan.model.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookStoreDetails(
    val isOnline: Boolean,
    val displayName: String,
    val status: String,
    val id: String
) : Parcelable
