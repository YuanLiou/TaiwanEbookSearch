package liou.rayyuan.ebooksearchtaiwan.appfunctions.model

import androidx.appfunctions.AppFunctionSerializable

@AppFunctionSerializable(isDescribedByKDoc = true)
data class BookOffer(
    /** Bookstore id from the API, for example readmoo or kobo. */
    val storeId: String,
    /** Human-readable store name for the agent. Not localized UI copy. */
    val storeName: String,
    /** Product title returned by the store. */
    val title: String,
    /** Author names when the API provided them. Empty if unknown. */
    val authors: List<String>,
    /**
     * Store price in [currency]. Null means unknown. Never treat null as free
     * or as 0. Prices are not converted across currencies.
     */
    val price: Double?,
    /** ISO-like currency code from the API, for example TWD. Null if missing. */
    val currency: String?,
    /** HTTPS product URL. Opening it is not a purchase. */
    val url: String,
    /** True when this offer is that store's first, highest-relevance candidate. */
    val isBestCandidate: Boolean
)
