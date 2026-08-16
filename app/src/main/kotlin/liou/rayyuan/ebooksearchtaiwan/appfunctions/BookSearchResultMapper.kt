package liou.rayyuan.ebooksearchtaiwan.appfunctions

import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.model.Book
import com.rayliu.commonmain.domain.model.BookSearchSlice
import com.rayliu.commonmain.domain.model.StoreResultSection
import liou.rayyuan.ebooksearchtaiwan.appfunctions.model.BookOffer
import liou.rayyuan.ebooksearchtaiwan.appfunctions.model.BookSearchResult
import liou.rayyuan.ebooksearchtaiwan.appfunctions.model.StoreSection

object BookSearchResultMapper {
    const val SNAPSHOT_URL_PREFIX = "https://taiwan-ebook-lover.github.io/searches/"

    fun map(
        slice: BookSearchSlice,
        isSnapshot: Boolean
    ): BookSearchResult =
        BookSearchResult(
            searchId = slice.searchId,
            query = slice.searchKeyword,
            snapshotUrl = snapshotUrlOrNull(slice.searchId),
            isSnapshot = isSnapshot,
            bestResults = slice.bestResults.map { it.toOffer(isBestCandidate = true) },
            storeSections = slice.storeSections.map { it.toSection() }
        )

    internal fun snapshotUrlOrNull(searchId: String): String? =
        searchId.trim().takeIf { it.isNotEmpty() }?.let { "$SNAPSHOT_URL_PREFIX$it" }

    internal fun unknownPriceToNull(price: Float): Double? = if (price < 0f) null else price.toDouble()

    internal fun storeDisplayName(store: DefaultStoreNames): String =
        when (store) {
            DefaultStoreNames.BOOK_COMPANY -> "books.com.tw"
            DefaultStoreNames.KINDLE -> "Kindle"
            DefaultStoreNames.READMOO -> "Readmoo"
            DefaultStoreNames.KOBO -> "Kobo"
            DefaultStoreNames.TAAZE -> "TAAZE"
            DefaultStoreNames.BOOK_WALKER -> "BOOKWALKER"
            DefaultStoreNames.PLAY_STORE -> "Google Play Books"
            DefaultStoreNames.PUBU -> "Pubu"
            DefaultStoreNames.HYREAD -> "HyRead"
            DefaultStoreNames.LIKERLAND -> "Liker Land"
            DefaultStoreNames.BEST_RESULT -> "Best result"
            DefaultStoreNames.UNKNOWN -> "Unknown"
        }

    private fun StoreResultSection.toSection(): StoreSection =
        StoreSection(
            storeId = store.defaultName,
            storeName = storeDisplayName(store),
            isOnline = isOnline,
            isOkay = isOkay,
            status = status,
            books = books.map { it.toOffer(isBestCandidate = false) }
        )

    private fun Book.toOffer(isBestCandidate: Boolean): BookOffer =
        BookOffer(
            storeId = bookStore.defaultName,
            storeName = storeDisplayName(bookStore),
            title = title,
            authors = authors.orEmpty(),
            price = unknownPriceToNull(price),
            currency = priceCurrency.trim().takeIf { it.isNotEmpty() },
            url = link,
            isBestCandidate = isBestCandidate
        )
}
