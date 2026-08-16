package liou.rayyuan.ebooksearchtaiwan.appfunctions.model

import androidx.appfunctions.AppFunctionSerializable

@AppFunctionSerializable(isDescribedByKDoc = true)
data class StoreSection(
    /** Bookstore id from the API, for example readmoo or kobo. */
    val storeId: String,
    /** Human-readable store name for the agent. */
    val storeName: String,
    /** Whether the remote store service reported itself online. */
    val isOnline: Boolean,
    /** Whether that store's search succeeded. */
    val isOkay: Boolean,
    /** Store status text from the API. May be empty. */
    val status: String,
    /**
     * Remaining titles from that store after the best candidate, ranks 2 to 11,
     * at most 10 books.
     */
    val books: List<BookOffer>
)
