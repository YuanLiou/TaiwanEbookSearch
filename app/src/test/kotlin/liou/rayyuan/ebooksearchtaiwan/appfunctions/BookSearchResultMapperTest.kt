package liou.rayyuan.ebooksearchtaiwan.appfunctions

import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.model.Book
import com.rayliu.commonmain.domain.model.BookSearchSlice
import com.rayliu.commonmain.domain.model.StoreResultSection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BookSearchResultMapperTest {
    @Test
    fun unknownPrice_mapsToNull_notZero() {
        val result = mapperResult(book = book(price = -1f))

        assertNull(result.bestResults.single().price)
    }

    @Test
    fun knownPrice_mapsToDouble_andKeepsCurrency() {
        val result = mapperResult(book = book(price = 120.5f, priceCurrency = "TWD"))

        assertEquals(
            120.5,
            result.bestResults.single().price ?: error("price is unknown"),
            0.0
        )
        assertEquals("TWD", result.bestResults.single().currency)
    }

    @Test
    fun emptySearchId_omitsSnapshotUrl() {
        val result = mapperResult(searchId = "")

        assertNull(result.snapshotUrl)
    }

    @Test
    fun presentSearchId_buildsSnapshotUrl() {
        val result = mapperResult(searchId = "abc")

        assertEquals(
            "https://taiwan-ebook-lover.github.io/searches/abc",
            result.snapshotUrl
        )
    }

    @Test
    fun liveSearch_setsIsSnapshotFalse() {
        val result = mapperResult()

        assertFalse(result.isSnapshot)
    }

    @Test
    fun snapshot_setsIsSnapshotTrue() {
        val result = mapperResult(isSnapshot = true)

        assertTrue(result.isSnapshot)
    }

    @Test
    fun storeSection_mapsStatusAndRemainingBooks() {
        val result =
            BookSearchResultMapper.map(
                slice =
                    BookSearchSlice(
                        searchId = "search-id",
                        searchKeyword = "query",
                        bestResults = emptyList(),
                        storeSections =
                            listOf(
                                StoreResultSection(
                                    store = DefaultStoreNames.READMOO,
                                    isOnline = true,
                                    isOkay = false,
                                    status = "temporarily unavailable",
                                    books = listOf(book(title = "remaining"))
                                )
                            )
                    ),
                isSnapshot = false
            )

        val section = result.storeSections.single()
        assertEquals("readmoo", section.storeId)
        assertEquals("temporarily unavailable", section.status)
        assertTrue(section.isOnline)
        assertFalse(section.isOkay)
        assertEquals(listOf("remaining"), section.books.map { it.title })
        assertFalse(section.books.single().isBestCandidate)
    }

    @Test
    fun nullAuthors_becomeEmptyList() {
        val result = mapperResult(book = book(authors = null))

        assertEquals(emptyList<String>(), result.bestResults.single().authors)
    }

    private fun mapperResult(
        searchId: String = "search-id",
        isSnapshot: Boolean = false,
        book: Book = book()
    ) = BookSearchResultMapper.map(
        slice =
            BookSearchSlice(
                searchId = searchId,
                searchKeyword = "query",
                bestResults = listOf(book.copy(isFirstChoice = true)),
                storeSections = emptyList()
            ),
        isSnapshot = isSnapshot
    )

    private fun book(
        title: String = "title",
        price: Float = 100f,
        priceCurrency: String = "TWD",
        authors: List<String>? = listOf("Author")
    ) = Book(
        thumbnail = "",
        priceCurrency = priceCurrency,
        price = price,
        link = "https://example.com/$title",
        about = "",
        id = title,
        title = title,
        authors = authors,
        bookStore = DefaultStoreNames.READMOO
    )
}
