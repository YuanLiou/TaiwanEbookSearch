package liou.rayyuan.ebooksearchtaiwan.model.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookStore(
    val bookStoreDetails: BookStoreDetails?,
    val books: List<Book>,
    val isOkay: Boolean,
    val status: String,
    val total: Int
) : Parcelable
