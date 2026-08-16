package liou.rayyuan.ebooksearchtaiwan.appfunctions.model

import androidx.appfunctions.AppFunctionSerializable

@AppFunctionSerializable(isDescribedByKDoc = true)
data class BookSearchResult(
    /** Snapshot id from the API. Empty when the API did not return one. */
    val searchId: String,
    /** Original search string. For a snapshot this is the saved keyword. */
    val query: String,
    /**
     * Public snapshot URL when [searchId] is present:
     * https://taiwan-ebook-lover.github.io/searches/{searchId}
     */
    val snapshotUrl: String?,
    /** True only when this result was loaded with getSearchSnapshot. */
    val isSnapshot: Boolean,
    /**
     * One candidate per successful store, sorted by numeric price low to high.
     * The first item is not guaranteed to be the same book across stores.
     */
    val bestResults: List<BookOffer>,
    /** Per-store status plus remaining titles after the best candidate. */
    val storeSections: List<StoreSection>
)
