package com.rayliu.commonmain.domain.repository

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.rayliu.commonmain.data.dao.SearchRecordDao
import com.rayliu.commonmain.data.dto.LocalSearchRecord
import com.rayliu.commonmain.data.mapper.LocalSearchRecordMapper
import com.rayliu.commonmain.data.mapper.SearchRecordMapper
import com.rayliu.commonmain.domain.Result
import com.rayliu.commonmain.domain.SimpleResult
import com.rayliu.commonmain.domain.model.SearchRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime

class SearchRecordRepositoryImpl(
    val searchRecordMapper: SearchRecordMapper,
    val localSearchRecordMapper: LocalSearchRecordMapper,
    val searchRecordDao: SearchRecordDao
) : SearchRecordRepository {
    override fun getPagingSearchRecordsFactory(): LiveData<PagedList<SearchRecord>> {
        val factory = searchRecordDao.getSearchRecordsPaged().map {
            searchRecordMapper.map(it)
        }
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(10)
            .setPageSize(10)
            .build()

        val pagedListBuilder = LivePagedListBuilder<Int, SearchRecord>(factory, config)
        return pagedListBuilder.build()
    }

    override suspend fun getSearchRecordsCounts(): SimpleResult<Int> = withContext(Dispatchers.IO) {
        val counts = searchRecordDao.getSearchRecordsCounts()
        Result.Success(counts)
    }

    override suspend fun saveKeywordToLocal(keyword: String) = withContext(Dispatchers.IO) {
        searchRecordDao.getSearchRecordWithTitle(keyword)?.let {
            searchRecordDao.updateCounts(it.id, it.counts + 1, OffsetDateTime.now())
        } ?: run {
            val searchRecord = LocalSearchRecord(keyword, 1, OffsetDateTime.now())
            searchRecordDao.insertRecords(listOf(searchRecord))
        }
    }

    override suspend fun deleteRecords(searchRecord: SearchRecord) = withContext(Dispatchers.IO) {
        searchRecordDao.deleteRecord(localSearchRecordMapper.map(searchRecord))
    }
}