package com.rayliu.commonmain.domain.usecase

import androidx.paging.PagingData
import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.model.Book
import com.rayliu.commonmain.domain.model.BookResult
import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.domain.model.SearchRecord
import com.rayliu.commonmain.domain.repository.BookRepository
import com.rayliu.commonmain.domain.repository.SearchRecordRepository
import com.rayliu.commonmain.domain.search.BlankSearchQueryException
import com.rayliu.commonmain.domain.search.NetworkUnavailableException
import com.rayliu.commonmain.domain.service.NetworkAvailability
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import junit.framework.TestCase.assertEquals
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchBooksUseCaseTest {
    @Test
    fun blankQuery_failsWithoutCallingRepositoryOrSavingHistory() =
        runBlocking {
            val fixture = fixture()

            val result = fixture.useCase("   ")

            assertTrue(result.exceptionOrNull() is BlankSearchQueryException)
            assertFalse(fixture.bookRepository.called.get())
            assertTrue(fixture.searchRecordRepository.savedKeywords.isEmpty())
        }

    @Test
    fun unavailableNetwork_failsWithoutCallingRepositoryOrSavingHistory() =
        runBlocking {
            val fixture = fixture(networkAvailable = false)

            val result = fixture.useCase("query")

            assertTrue(result.exceptionOrNull() is NetworkUnavailableException)
            assertFalse(fixture.bookRepository.called.get())
            assertTrue(fixture.searchRecordRepository.savedKeywords.isEmpty())
        }

    @Test
    fun validQuery_usesEnabledStoresAndPreservesOriginalQuery() =
        runBlocking {
            val fixture = fixture()

            val result = fixture.useCase("9789861755267")

            assertTrue(result.isSuccess)
            val slice = result.getOrThrow()
            assertEquals(listOf(DefaultStoreNames.READMOO), fixture.bookRepository.lastStores)
            assertEquals("9789861755267", fixture.bookRepository.lastKeyword)
            assertEquals(listOf("9789861755267"), fixture.searchRecordRepository.savedKeywords)
            assertEquals("search-id", slice.searchId)
            assertEquals("query", slice.searchKeyword)
        }

    @Test
    fun validQuery_returnsPartialSuccessFromRepository() =
        runBlocking {
            val fixture = fixture()
            fixture.bookRepository.result =
                Result.success(
                    bookStores(
                        readmoo =
                            BookResult(
                                books = listOf(book("readmoo", 120f, DefaultStoreNames.READMOO)),
                                isOnline = true,
                                isOkay = true
                            )
                    )
                )

            val result = fixture.useCase("query").getOrThrow()

            assertEquals(listOf("readmoo"), result.bestResults.map { it.title })
            assertTrue(result.storeSections.single().isOkay)
        }

    @Test
    fun repeatedQuery_callsRepositoryForEachRequest() =
        runBlocking {
            val fixture = fixture()

            fixture.useCase("query").getOrThrow()
            fixture.useCase("query").getOrThrow()

            assertEquals(2, fixture.bookRepository.calls.get())
        }

    private fun fixture(
        networkAvailable: Boolean = true,
        sortByPrice: Boolean = true
    ): Fixture {
        val bookRepository = FakeBookRepository()
        val searchRecordRepository = FakeSearchRecordRepository()
        val getBooksWithStoresUseCase =
            GetBooksWithStoresUseCase(
                bookRepository = bookRepository,
                searchRecordRepository = searchRecordRepository
            )
        val useCase =
            SearchBooksUseCase(
                getDefaultBookSortUseCase =
                    GetDefaultBookSortUseCase {
                        flowOf(persistentListOf(DefaultStoreNames.READMOO))
                    },
                getBooksWithStoresUseCase = getBooksWithStoresUseCase,
                networkAvailability = NetworkAvailability { networkAvailable },
                userPreferenceManager = FakeUserPreferenceManager(sortByPrice)
            )
        return Fixture(useCase, bookRepository, searchRecordRepository)
    }

    private fun bookStores(readmoo: BookResult? = null) =
        BookStores(
            searchId = "search-id",
            searchKeyword = "query",
            booksCompany = null,
            readmoo = readmoo,
            kobo = null,
            taaze = null,
            bookWalker = null,
            playStore = null,
            pubu = null,
            hyread = null,
            kindle = null,
            likerLand = null
        )

    private fun book(
        title: String,
        price: Float,
        store: DefaultStoreNames
    ) = Book(
        thumbnail = "",
        priceCurrency = "TWD",
        price = price,
        link = "https://example.com/$title",
        about = "",
        id = title,
        title = title,
        authors = listOf("Author"),
        bookStore = store
    )

    private data class Fixture(
        val useCase: SearchBooksUseCase,
        val bookRepository: FakeBookRepository,
        val searchRecordRepository: FakeSearchRecordRepository
    )

    private inner class FakeBookRepository : BookRepository {
        val called = AtomicBoolean(false)
        val calls = AtomicInteger(0)
        var lastStores: List<DefaultStoreNames> = emptyList()
        var lastKeyword: String? = null
        var result: Result<BookStores> = Result.success(bookStores())

        override suspend fun getBooks(keyword: String): Result<BookStores> = error("not used")

        override suspend fun getBooksWithStores(
            stores: List<DefaultStoreNames>,
            keyword: String
        ): Result<BookStores> {
            called.set(true)
            calls.incrementAndGet()
            lastStores = stores
            lastKeyword = keyword
            return result
        }

        override fun getDefaultResultSort(): Flow<ImmutableList<DefaultStoreNames>> = error("not used")

        override suspend fun saveDefaultResultSort(currentSortSettings: ImmutableList<DefaultStoreNames>) = Unit

        override suspend fun getSearchSnapshot(searchId: String): Result<BookStores> = error("not used")
    }

    private class FakeSearchRecordRepository : SearchRecordRepository {
        val savedKeywords = mutableListOf<String>()

        override fun getPagingSearchRecordsFactory(): Flow<PagingData<SearchRecord>> = error("not used")

        override suspend fun getSearchRecordsCounts(): Result<Int> = Result.success(0)

        override suspend fun saveKeywordToLocal(keyword: String) {
            savedKeywords += keyword
        }

        override suspend fun deleteRecords(searchRecord: SearchRecord) = Unit

        override suspend fun deleteAllRecords() = Unit
    }

    private class FakeUserPreferenceManager(
        private val sortByPrice: Boolean
    ) : UserPreferenceManager {
        override fun isFollowSystemTheme(): Boolean = false

        override fun isDarkTheme(): Boolean = false

        override fun isPreferCustomTab(): Boolean = false

        override fun isSearchResultSortByPrice(): Boolean = sortByPrice
    }
}
