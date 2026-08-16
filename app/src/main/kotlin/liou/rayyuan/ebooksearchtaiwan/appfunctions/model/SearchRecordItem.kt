package liou.rayyuan.ebooksearchtaiwan.appfunctions.model

import androidx.appfunctions.AppFunctionSerializable

@AppFunctionSerializable(isDescribedByKDoc = true)
data class SearchRecordItem(
    /** Exact local search string. This is personal device history, not a bookshelf. */
    val query: String,
    /** ISO-8601 last-search time with offset. Always present. */
    val lastSearchedAt: String,
    /** How many times this exact string was searched on this device. */
    val times: Int
)
