package com.rayliu.commonmain.domain.usecase

import androidx.paging.PagingData
import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.model.Book
import com.rayliu.commonmain.domain.model.BookResult
import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.domain.model.RecentSearchRecord
import com.rayliu.commonmain.domain.model.SearchRecord
import com.rayliu.commonmain.domain.repository.BookRepository
import com.rayliu.commonmain.domain.repository.SearchRecordRepository
import com.rayliu.commonmain.domain.search.BlankSearchIdException
import com.rayliu.commonmain.domain.search.NetworkUnavailableException
import com.rayliu.commonmain.domain.search.SearchSnapshotNotFoundException
import com.rayliu.commonmain.domain.service.NetworkAvailability
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GetSlicedSearchSnapshotUseCaseTest {
    @Test
    fun blankSearchId_failsWithoutRepositoryOrHistory() =
        runBlocking {
            val fixture = fixture()

            val result = fixture.useCase("   ")

            assertTrue(result.exceptionOrNull() is BlankSearchIdException)
            assertFalse(fixture.bookRepository.snapshotCalled.get())
            assertFalse(fixture.bookRepository.liveSearchCalled.get())
            assertTrue(fixture.searchRecordRepository.savedKeywords.isEmpty())
        }

    @Test
    fun unavailableNetwork_failsWithoutRepositoryOrHistory() =
        runBlocking {
            val fixture = fixture(networkAvailable = false)

            val result = fixture.useCase("search-id")

            assertTrue(result.exceptionOrNull() is NetworkUnavailableException)
            assertFalse(fixture.bookRepository.snapshotCalled.get())
            assertFalse(fixture.bookRepository.liveSearchCalled.get())
            assertTrue(fixture.searchRecordRepository.savedKeywords.isEmpty())
        }

    @Test
    fun success_slicesResult_andDoesNotCallLiveSearch() =
        runBlocking {
            val fixture = fixture()
            fixture.bookRepository.snapshotResult =
                Result.success(
                    bookStores(
                        readmoo =
                            BookResult(
                                books =
                                    listOf(
                                        book("first", 100f),
                                        book("second", 200f)
                                    ),
                                isOnline = true,
                                isOkay = true
                            )
                    )
                )

            val result = fixture.useCase("search-id").getOrThrow()

            assertTrue(fixture.bookRepository.snapshotCalled.get())
            assertFalse(fixture.bookRepository.liveSearchCalled.get())
            assertEquals(listOf("first"), result.bestResults.map { it.title })
            assertEquals(listOf("second"), result.storeSections.single().books.map { it.title })
        }

    @Test
    fun repositoryFailure_doesNotSaveHistory() =
        runBlocking {
            val fixture = fixture()
            fixture.bookRepository.snapshotResult =
                Result.failure(IllegalStateException("snapshot failed"))

            val result = fixture.useCase("search-id")

            assertTrue(result.isFailure)
            assertTrue(fixture.bookRepository.snapshotCalled.get())
            assertFalse(fixture.bookRepository.liveSearchCalled.get())
            assertTrue(fixture.searchRecordRepository.savedKeywords.isEmpty())
        }

    @Test
    fun emptySnapshot_failsAsNotFound_withoutSavingHistory() =
        runBlocking {
            val fixture = fixture()
            fixture.bookRepository.snapshotResult =
                Result.success(bookStores(searchId = "", searchKeyword = ""))

            val result = fixture.useCase("search-id")

            assertTrue(result.exceptionOrNull() is SearchSnapshotNotFoundException)
            assertTrue(fixture.bookRepository.snapshotCalled.get())
            assertTrue(fixture.searchRecordRepository.savedKeywords.isEmpty())
        }

    @Test
    fun snapshotWithoutId_failsAsNotFound_withoutSavingHistory() =
        runBlocking {
            val fixture = fixture()
            fixture.bookRepository.snapshotResult =
                Result.success(bookStores(searchId = "", searchKeyword = "query"))

            val result = fixture.useCase("search-id")

            assertTrue(result.exceptionOrNull() is SearchSnapshotNotFoundException)
            assertTrue(fixture.searchRecordRepository.savedKeywords.isEmpty())
        }

    @Test
    fun success_savesSnapshotKeywordThroughExistingUseCase() =
        runBlocking {
            val fixture = fixture()

            fixture.useCase("search-id").getOrThrow()

            assertEquals(listOf("query"), fixture.searchRecordRepository.savedKeywords)
        }

    private fun fixture(networkAvailable: Boolean = true): Fixture {
        val bookRepository = FakeBookRepository()
        val searchRecordRepository = FakeSearchRecordRepository()
        val snapshotUseCase = GetSearchSnapshotUseCase(bookRepository, searchRecordRepository)
        val useCase =
            GetSlicedSearchSnapshotUseCase(
                getSearchSnapshotUseCase = snapshotUseCase,
                getDefaultBookSortUseCase =
                    GetDefaultBookSortUseCase {
                        flowOf(persistentListOf(DefaultStoreNames.READMOO))
                    },
                networkAvailability = NetworkAvailability { networkAvailable },
                userPreferenceManager = FakeUserPreferenceManager()
            )
        return Fixture(useCase, bookRepository, searchRecordRepository)
    }

    private fun bookStores(
        searchId: String = "search-id",
        searchKeyword: String = "query",
        readmoo: BookResult? = null
    ) = BookStores(
        searchId = searchId,
        searchKeyword = searchKeyword,
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
        price: Float
    ) = Book(
        thumbnail = "",
        priceCurrency = "TWD",
        price = price,
        link = "https://example.com/$title",
        about = "",
        id = title,
        title = title,
        authors = listOf("Author"),
        bookStore = DefaultStoreNames.READMOO
    )

    private data class Fixture(
        val useCase: GetSlicedSearchSnapshotUseCase,
        val bookRepository: FakeBookRepository,
        val searchRecordRepository: FakeSearchRecordRepository
    )

    private inner class FakeBookRepository : BookRepository {
        val snapshotCalled = AtomicBoolean(false)
        val liveSearchCalled = AtomicBoolean(false)
        var snapshotResult: Result<BookStores> = Result.success(bookStores())

        override suspend fun getBooks(keyword: String): Result<BookStores> {
            liveSearchCalled.set(true)
            error("live search must not be called")
        }

        override suspend fun getBooksWithStores(
            stores: List<DefaultStoreNames>,
            keyword: String
        ): Result<BookStores> {
            liveSearchCalled.set(true)
            error("live search must not be called")
        }

        override fun getDefaultResultSort(): Flow<ImmutableList<DefaultStoreNames>> = error("not used")

        override suspend fun saveDefaultResultSort(currentSortSettings: ImmutableList<DefaultStoreNames>) = Unit

        override suspend fun getSearchSnapshot(searchId: String): Result<BookStores> {
            snapshotCalled.set(true)
            return snapshotResult
        }
    }

    private class FakeSearchRecordRepository : SearchRecordRepository {
        val savedKeywords = mutableListOf<String>()

        override fun getPagingSearchRecordsFactory(): Flow<PagingData<SearchRecord>> = error("not used")

        override suspend fun getSearchRecordsCounts(): Result<Int> = Result.success(0)

        override suspend fun saveKeywordToLocal(keyword: String) {
            savedKeywords += keyword
        }

        override suspend fun getRecentSearchRecords(limit: Int): List<RecentSearchRecord> = emptyList()

        override suspend fun deleteRecords(searchRecord: SearchRecord) = Unit

        override suspend fun deleteAllRecords() = Unit
    }

    private class FakeUserPreferenceManager : UserPreferenceManager {
        override fun isFollowSystemTheme(): Boolean = false

        override fun isDarkTheme(): Boolean = false

        override fun isPreferCustomTab(): Boolean = false

        override fun isSearchResultSortByPrice(): Boolean = true
    }
}
