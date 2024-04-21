package com.rayliu.commonmain.domain.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.rayliu.commonmain.data.dao.SearchRecordDao
import com.rayliu.commonmain.data.mapper.LocalSearchRecordMapper
import com.rayliu.commonmain.domain.model.SearchRecord
import org.threeten.bp.OffsetDateTime

class SearchRecordRepositoryImpl(
    private val localSearchRecordMapper: LocalSearchRecordMapper,
    private val searchRecordDao: SearchRecordDao
) : SearchRecordRepository {
    private val pageSize = 10

    override fun getPagingSearchRecordsFactory(): LiveData<PagingData<SearchRecord>> {
        val pager =
            Pager(
                config =
                    PagingConfig(
                        pageSize = pageSize,
                        initialLoadSize = pageSize,
                        enablePlaceholders = true
                    ),
                pagingSourceFactory = {
                    searchRecordDao.getSearchRecordsPaged()
                }
            )
        return pager.liveData
    }

    override suspend fun getSearchRecordsCounts(): Result<Int> = runCatching { searchRecordDao.getSearchRecordsCounts() }

    override suspend fun saveKeywordToLocal(keyword: String) {
        val record = searchRecordDao.getSearchRecordWithTitle(keyword)
        if (record != null) {
            val recordId = record.id
            if (recordId != null) {
                searchRecordDao.updateCounts(
                    recordId,
                    record.counts + 1,
                    OffsetDateTime.now()
                )
            }
            return
        }

        val searchRecord = SearchRecord(null, 1, keyword)
        searchRecordDao.insertRecords(
            listOf(
                localSearchRecordMapper.map(searchRecord)
            )
        )
    }

    override suspend fun deleteRecords(searchRecord: SearchRecord) {
        searchRecordDao.deleteRecord(localSearchRecordMapper.map(searchRecord))
    }
}
