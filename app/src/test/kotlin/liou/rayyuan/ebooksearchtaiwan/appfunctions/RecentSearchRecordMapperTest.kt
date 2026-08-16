package liou.rayyuan.ebooksearchtaiwan.appfunctions

import com.rayliu.commonmain.OffsetDateTimeHelper
import com.rayliu.commonmain.domain.model.RecentSearchRecord
import liou.rayyuan.ebooksearchtaiwan.appfunctions.model.SearchRecordItem
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RecentSearchRecordMapperTest {
    @Test
    fun queryAndTimes_areCopied() {
        val result = RecentSearchRecordMapper.map(record(), OffsetDateTimeHelper())

        assertEquals("query", result.query)
        assertEquals(3, result.times)
    }

    @Test
    fun localDateTime_mapsToIso8601Timestamp() {
        val record = record()
        val result = RecentSearchRecordMapper.map(record, OffsetDateTimeHelper())

        assertEquals(
            OffsetDateTimeHelper().provideTimeStampString(record.lastSearchedAt),
            result.lastSearchedAt
        )
        assertTrue(result.lastSearchedAt.startsWith("2026-08-16T"))
    }

    @Test
    fun searchRecordItem_doesNotExposeSqlId() {
        assertFalse(SearchRecordItem::class.java.declaredFields.any { it.name == "id" })
    }

    private fun record() =
        RecentSearchRecord(
            query = "query",
            lastSearchedAt = LocalDateTime(2026, 8, 16, 10, 0, 0),
            times = 3
        )
}
