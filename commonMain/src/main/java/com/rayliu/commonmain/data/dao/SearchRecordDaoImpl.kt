package com.rayliu.commonmain.data.dao

import app.cash.paging.PagingSource
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.paging3.QueryPagingSource
import com.rayliu.commonmain.OffsetDateTimeHelper
import com.rayliu.commonmain.data.database.EbookTwDatabase
import com.rayliu.commonmain.data.dto.LocalSearchRecord
import com.rayliu.commonmain.data.mapper.SearchRecordMapper
import com.rayliu.commonmain.domain.model.SearchRecord
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime

class SearchRecordDaoImpl(
    private val offsetDateTimeHelper: OffsetDateTimeHelper,
    private val searchRecordMapper: SearchRecordMapper,
    private val ioDispatcher: CoroutineDispatcher,
    private val defaultDispatcher: CoroutineDispatcher,
    database: EbookTwDatabase,
) : SearchRecordDao {
    private val queries = database.searchRecordsQueries

    override fun getAllSearchRecords(): Flow<LocalSearchRecord> =
        queries.getAllSearchRecords().asFlow().mapToOne(defaultDispatcher).map {
            val timeStamp =
                if (it.time_stamp != null) {
                    offsetDateTimeHelper.convertToLocalDateTime(it.time_stamp)
                } else {
                    offsetDateTimeHelper.provideCurrentMoment()
                }
            LocalSearchRecord(it.result_text, it.counts, timeStamp).also { localSearchRecord ->
                localSearchRecord.id = it.id
            }
        }

    override fun getSearchRecordsPaged(): PagingSource<Int, SearchRecord> =
        QueryPagingSource(
            countQuery = queries.getSearchRecordsCounts(),
            transacter = queries,
            context = ioDispatcher,
            queryProvider = { offset, limit ->
                queries.getPagingSearchRecords(offset, limit) { id, resultText, timeStamps, counts ->
                    val timeStamp =
                        if (timeStamps != null) {
                            offsetDateTimeHelper.convertToLocalDateTime(timeStamps)
                        } else {
                            offsetDateTimeHelper.provideCurrentMoment()
                        }

                    val localSearchRecord =
                        LocalSearchRecord(resultText, counts, timeStamp).also {
                            it.id = id
                        }
                    searchRecordMapper.map(localSearchRecord)
                }
            },
        )

    override suspend fun getSearchRecordWithTitle(passedRecord: String): LocalSearchRecord? =
        withContext(ioDispatcher) {
            queries.getSearchRecordWithTitle(passedRecord).executeAsOneOrNull()?.run {
                val timeStamp =
                    if (time_stamp != null) {
                        offsetDateTimeHelper.convertToLocalDateTime(time_stamp)
                    } else {
                        offsetDateTimeHelper.provideCurrentMoment()
                    }
                LocalSearchRecord(result_text, counts, timeStamp).also { localSearchRecord ->
                    localSearchRecord.id = id
                }
            }
        }

    override suspend fun insertRecords(searchRecords: List<LocalSearchRecord>) =
        withContext(ioDispatcher) {
            for (record in searchRecords) {
                if (record.timeStamps == null) {
                    continue
                }

                queries.insertRecord(
                    id = null,
                    result_text = record.resultText,
                    counts = record.counts,
                    time_stamp = offsetDateTimeHelper.provideTimeStampString(record.timeStamps)
                )
            }
        }

    override suspend fun deleteRecord(searchRecord: LocalSearchRecord) {
        withContext(ioDispatcher) {
            searchRecord.id?.run { queries.deleteRecord(this) }
        }
    }

    override suspend fun updateRecord(searchRecord: LocalSearchRecord) =
        withContext(ioDispatcher) {
            val id = searchRecord.id ?: return@withContext
            val timeStamp = searchRecord.timeStamps ?: return@withContext
            queries.updateRecord(
                id = id,
                result_text = searchRecord.resultText,
                counts = searchRecord.counts,
                time_stamp = offsetDateTimeHelper.provideTimeStampString(timeStamp)
            )
        }

    override suspend fun getSearchRecordsCounts(): Int =
        withContext(ioDispatcher) {
            queries.getSearchRecordsCounts().executeAsOneOrNull()?.toInt() ?: 0
        }

    override suspend fun updateCounts(
        id: Long,
        counts: Long,
        timeStamp: LocalDateTime
    ) = withContext(ioDispatcher) {
        queries.updateCounts(
            id = id,
            counts = counts,
            timeStamp = offsetDateTimeHelper.provideTimeStampString(timeStamp)
        )
    }

    override suspend fun deleteAllRecords() =
        withContext(ioDispatcher) {
            queries.deleteAllRecords()
        }
}
