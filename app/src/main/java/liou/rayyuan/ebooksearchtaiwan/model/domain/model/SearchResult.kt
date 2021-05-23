package liou.rayyuan.ebooksearchtaiwan.model.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchResult(
    val keyword: String,
    val apiVersion: String,
    val bookStores: List<BookStore>,
    val totalBookCounts: Int
) : Parcelable
