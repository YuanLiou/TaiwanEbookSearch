package com.rayliu.commonmain.data.dao

import app.cash.paging.PagingSource
import com.rayliu.commonmain.data.dto.LocalSearchRecord
import com.rayliu.commonmain.domain.model.SearchRecord
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.OffsetDateTime

interface SearchRecordDao {
    fun getAllSearchRecords(): Flow<LocalSearchRecord>

    fun getSearchRecordsPaged(): PagingSource<Int, SearchRecord>

    suspend fun getSearchRecordWithTitle(passedRecord: String): LocalSearchRecord?

    suspend fun insertRecords(searchRecords: List<LocalSearchRecord>)

    suspend fun deleteRecord(searchRecord: LocalSearchRecord)

    suspend fun updateRecord(searchRecord: LocalSearchRecord)

    suspend fun getSearchRecordsCounts(): Int

    suspend fun updateCounts(
        id: Long,
        counts: Long,
        timeStamp: OffsetDateTime?
    )

    suspend fun deleteAllRecords()
}
