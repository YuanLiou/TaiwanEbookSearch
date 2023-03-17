package com.rayliu.commonmain.domain.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
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
    private val searchRecordMapper: SearchRecordMapper,
    private val localSearchRecordMapper: LocalSearchRecordMapper,
    private val searchRecordDao: SearchRecordDao
) : SearchRecordRepository {

    private val pageSize = 10

    override fun getPagingSearchRecordsFactory(): LiveData<PagingData<SearchRecord>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = pageSize,
                initialLoadSize = pageSize,
                enablePlaceholders = true
            ),
            pagingSourceFactory = searchRecordDao.getSearchRecordsPaged().map {
                searchRecordMapper.map(it)
            }.asPagingSourceFactory()

        )
        return pager.liveData
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