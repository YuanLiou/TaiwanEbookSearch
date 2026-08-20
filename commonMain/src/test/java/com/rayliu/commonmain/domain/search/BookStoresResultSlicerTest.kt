package com.rayliu.commonmain.domain.search

import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.model.Book
import com.rayliu.commonmain.domain.model.BookResult
import com.rayliu.commonmain.domain.model.BookStores
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BookStoresResultSlicerTest {
    @Test
    fun bestResults_useFirstBookFromEachStore_andSortByPrice() {
        val bookStores =
            bookStores(
                readmoo =
                    bookResult(
                        DefaultStoreNames.READMOO,
                        listOf(
                            book("readmoo-expensive", 300f, DefaultStoreNames.READMOO),
                            book("readmoo-cheap", 100f, DefaultStoreNames.READMOO)
                        )
                    ),
                kobo =
                    bookResult(
                        DefaultStoreNames.KOBO,
                        listOf(book("kobo-middle", 200f, DefaultStoreNames.KOBO))
                    )
            )

        val slice =
            BookStoresResultSlicer.slice(
                bookStores,
                listOf(DefaultStoreNames.READMOO, DefaultStoreNames.KOBO),
                sortStoreBooksByPrice = true
            )

        assertEquals(
            listOf("kobo-middle", "readmoo-expensive"),
            slice.bestResults.map { it.title }
        )
    }

    @Test
    fun storeSection_dropsFirstBook_andTakesAtMostTenBooks() {
        val books =
            (0..11).map {
                book(
                    title = "book-$it",
                    price = it.toFloat(),
                    store = DefaultStoreNames.READMOO,
                    similarity = it
                )
            }

        val slice =
            BookStoresResultSlicer.slice(
                bookStores(readmoo = bookResult(DefaultStoreNames.READMOO, books)),
                listOf(DefaultStoreNames.READMOO),
                sortStoreBooksByPrice = true
            )

        assertEquals(10, slice.storeSections.single().books.size)
        assertEquals((1..10).map { "book-$it" }, slice.storeSections.single().books.map { it.title })
    }

    @Test
    fun storeWithOneBook_hasBestResult_andEmptyStoreSection() {
        val slice =
            BookStoresResultSlicer.slice(
                bookStores(
                    readmoo =
                        bookResult(
                            DefaultStoreNames.READMOO,
                            listOf(book("only-book", 100f, DefaultStoreNames.READMOO))
                        )
                ),
                listOf(DefaultStoreNames.READMOO),
                sortStoreBooksByPrice = true
            )

        assertEquals(listOf("only-book"), slice.bestResults.map { it.title })
        assertTrue(slice.storeSections.single().books.isEmpty())
    }

    @Test
    fun storeSection_withoutPriceSorting_sortsByTitleSimilarity() {
        val slice =
            BookStoresResultSlicer.slice(
                bookStores(
                    readmoo =
                        bookResult(
                            DefaultStoreNames.READMOO,
                            listOf(
                                book("first", 100f, DefaultStoreNames.READMOO, similarity = 0),
                                book("low", 30f, DefaultStoreNames.READMOO, similarity = 1),
                                book("high", 10f, DefaultStoreNames.READMOO, similarity = 9),
                                book("middle", 20f, DefaultStoreNames.READMOO, similarity = 5)
                            )
                        )
                ),
                listOf(DefaultStoreNames.READMOO),
                sortStoreBooksByPrice = false
            )

        assertEquals(
            listOf("high", "middle", "low"),
            slice.storeSections.single().books.map { it.title }
        )
    }

    @Test
    fun storeSection_withPriceSorting_sortsByPrice() {
        val slice =
            BookStoresResultSlicer.slice(
                bookStores(
                    readmoo =
                        bookResult(
                            DefaultStoreNames.READMOO,
                            listOf(
                                book("first", 100f, DefaultStoreNames.READMOO),
                                book("expensive", 50f, DefaultStoreNames.READMOO),
                                book("cheap", 10f, DefaultStoreNames.READMOO),
                                book("middle", 30f, DefaultStoreNames.READMOO)
                            )
                        )
                ),
                listOf(DefaultStoreNames.READMOO),
                sortStoreBooksByPrice = true
            )

        assertEquals(
            listOf("cheap", "middle", "expensive"),
            slice.storeSections.single().books.map { it.title }
        )
    }

    @Test
    fun unknownPrice_remainsNegativeOne() {
        val slice =
            BookStoresResultSlicer.slice(
                bookStores(
                    readmoo =
                        bookResult(
                            DefaultStoreNames.READMOO,
                            listOf(book("unknown", -1f, DefaultStoreNames.READMOO))
                        )
                ),
                listOf(DefaultStoreNames.READMOO),
                sortStoreBooksByPrice = true
            )

        assertEquals(-1f, slice.bestResults.single().price)
    }

    @Test
    fun failedStore_keepsOtherStoreResults_andSectionStatus() {
        val slice =
            BookStoresResultSlicer.slice(
                bookStores(
                    readmoo =
                        bookResult(
                            store = DefaultStoreNames.READMOO,
                            books = emptyList(),
                            isOnline = false,
                            isOkay = false,
                            status = "error"
                        ),
                    kobo =
                        bookResult(
                            DefaultStoreNames.KOBO,
                            listOf(book("kobo-book", 150f, DefaultStoreNames.KOBO))
                        )
                ),
                listOf(DefaultStoreNames.READMOO, DefaultStoreNames.KOBO),
                sortStoreBooksByPrice = true
            )

        assertEquals(listOf("kobo-book"), slice.bestResults.map { it.title })
        val readmooSection = slice.storeSections.first()
        assertFalse(readmooSection.isOkay)
        assertEquals("error", readmooSection.status)
        assertTrue(readmooSection.books.isEmpty())
    }

    @Test
    fun disabledStore_isNotIncluded() {
        val slice =
            BookStoresResultSlicer.slice(
                bookStores(
                    readmoo =
                        bookResult(
                            DefaultStoreNames.READMOO,
                            listOf(book("readmoo", 200f, DefaultStoreNames.READMOO))
                        ),
                    kobo =
                        bookResult(
                            DefaultStoreNames.KOBO,
                            listOf(book("kobo", 100f, DefaultStoreNames.KOBO))
                        )
                ),
                listOf(DefaultStoreNames.READMOO),
                sortStoreBooksByPrice = true
            )

        assertEquals(listOf("readmoo"), slice.bestResults.map { it.title })
        assertEquals(listOf(DefaultStoreNames.READMOO), slice.storeSections.map { it.store })
    }

    @Test
    fun storeSections_followEnabledStoreOrder() {
        val slice =
            BookStoresResultSlicer.slice(
                bookStores(
                    readmoo =
                        bookResult(
                            DefaultStoreNames.READMOO,
                            listOf(book("readmoo", 200f, DefaultStoreNames.READMOO))
                        ),
                    kobo =
                        bookResult(
                            DefaultStoreNames.KOBO,
                            listOf(book("kobo", 100f, DefaultStoreNames.KOBO))
                        )
                ),
                listOf(DefaultStoreNames.KOBO, DefaultStoreNames.READMOO),
                sortStoreBooksByPrice = true
            )

        assertEquals(
            listOf(DefaultStoreNames.KOBO, DefaultStoreNames.READMOO),
            slice.storeSections.map { it.store }
        )
    }

    @Test
    fun bestResult_isCopied_withoutMutatingOriginalBook() {
        val originalBook = book("original", 100f, DefaultStoreNames.READMOO)
        val bookStores = bookStores(readmoo = bookResult(DefaultStoreNames.READMOO, listOf(originalBook)))

        val slice =
            BookStoresResultSlicer.slice(
                bookStores,
                listOf(DefaultStoreNames.READMOO),
                sortStoreBooksByPrice = true
            )

        assertFalse(bookStores.readmoo!!.books.first().isFirstChoice)
        assertTrue(slice.bestResults.first().isFirstChoice)
    }

    private fun bookStores(
        readmoo: BookResult? = null,
        kobo: BookResult? = null
    ) = BookStores(
        searchId = "search-id",
        searchKeyword = "query",
        booksCompany = null,
        readmoo = readmoo,
        kobo = kobo,
        taaze = null,
        bookWalker = null,
        playStore = null,
        pubu = null,
        hyread = null,
        kindle = null,
        likerLand = null
    )

    private fun bookResult(
        store: DefaultStoreNames,
        books: List<Book>,
        isOnline: Boolean = true,
        isOkay: Boolean = true,
        status: String = ""
    ) = BookResult(
        books = books,
        isOnline = isOnline,
        isOkay = isOkay,
        status = status
    )

    private fun book(
        title: String,
        price: Float,
        store: DefaultStoreNames,
        similarity: Int? = null
    ) = Book(
        thumbnail = "",
        priceCurrency = "TWD",
        price = price,
        link = "https://example.com/$title",
        about = "",
        id = title,
        title = title,
        authors = listOf("Author"),
        bookStore = store,
        isFirstChoice = false,
        titleKeywordSimilarity = similarity
    )
}
