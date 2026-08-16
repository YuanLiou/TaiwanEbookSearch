package com.rayliu.commonmain.domain.usecase

import androidx.paging.PagingData
import com.rayliu.commonmain.domain.model.RecentSearchRecord
import com.rayliu.commonmain.domain.model.SearchRecord
import com.rayliu.commonmain.domain.repository.SearchRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetRecentSearchRecordsUseCaseTest {
    @Test
    fun nonPositiveLimit_requestsDefaultLimit() =
        runBlocking {
            val fixture = fixture()

            fixture.useCase(0)
            fixture.useCase(-3)

            assertEquals(listOf(10, 10), fixture.repository.requestedLimits)
        }

    @Test
    fun limitAboveMaximum_requestsMaximumLimit() =
        runBlocking {
            val fixture = fixture()

            fixture.useCase(21)

            assertEquals(listOf(20), fixture.repository.requestedLimits)
        }

    @Test
    fun validLimit_requestsRequestedLimit() =
        runBlocking {
            val fixture = fixture()

            fixture.useCase(4)

            assertEquals(listOf(4), fixture.repository.requestedLimits)
        }

    @Test
    fun preservesRepositoryNewestFirstOrder() =
        runBlocking {
            val newest = record("newest", LocalDateTime(2026, 8, 16, 10, 0))
            val older = record("older", LocalDateTime(2026, 8, 15, 10, 0))
            val fixture = fixture(listOf(newest, older))

            val result = fixture.useCase(2)

            assertEquals(listOf(newest, older), result)
        }

    @Test
    fun emptyRepository_returnsEmptyList() =
        runBlocking {
            val fixture = fixture()

            val result = fixture.useCase()

            assertTrue(result.isEmpty())
        }

    @Test
    fun returnedRecords_doNotExposeSqlIdentifiers() =
        runBlocking {
            val expected = record("query", LocalDateTime(2026, 8, 16, 10, 0))
            val fixture = fixture(listOf(expected))

            val result = fixture.useCase()

            assertEquals(listOf(expected), result)
        }

    private fun fixture(records: List<RecentSearchRecord> = emptyList()): Fixture {
        val repository = FakeSearchRecordRepository(records)
        return Fixture(GetRecentSearchRecordsUseCase(repository), repository)
    }

    private fun record(
        query: String,
        lastSearchedAt: LocalDateTime
    ) = RecentSearchRecord(
        query = query,
        lastSearchedAt = lastSearchedAt,
        times = 1
    )

    private data class Fixture(
        val useCase: GetRecentSearchRecordsUseCase,
        val repository: FakeSearchRecordRepository
    )

    private class FakeSearchRecordRepository(
        private val records: List<RecentSearchRecord>
    ) : SearchRecordRepository {
        val requestedLimits = mutableListOf<Int>()

        override fun getPagingSearchRecordsFactory(): Flow<PagingData<SearchRecord>> = error("not used")

        override suspend fun getSearchRecordsCounts(): Result<Int> = Result.success(0)

        override suspend fun saveKeywordToLocal(keyword: String) = Unit

        override suspend fun getRecentSearchRecords(limit: Int): List<RecentSearchRecord> {
            requestedLimits += limit
            return records
        }

        override suspend fun deleteRecords(searchRecord: SearchRecord) = Unit

        override suspend fun deleteAllRecords() = Unit
    }
}
