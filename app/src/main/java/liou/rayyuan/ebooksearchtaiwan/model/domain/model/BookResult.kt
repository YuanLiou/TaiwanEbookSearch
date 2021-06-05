package liou.rayyuan.ebooksearchtaiwan.model.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookResult(
    val books: List<Book>,
    val isOnline: Boolean,
    val isOkay: Boolean,
    val status: String = ""
) : Parcelable
