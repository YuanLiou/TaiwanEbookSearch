package com.rayliu.commonmain.domain.repository

import androidx.paging.PagingData
import com.rayliu.commonmain.domain.model.SearchRecord
import kotlinx.coroutines.flow.Flow

interface SearchRecordRepository {
    fun getPagingSearchRecordsFactory(): Flow<PagingData<SearchRecord>>

    suspend fun getSearchRecordsCounts(): Result<Int>

    suspend fun saveKeywordToLocal(keyword: String)

    suspend fun deleteRecords(searchRecord: SearchRecord)

    suspend fun deleteAllRecords()
}
