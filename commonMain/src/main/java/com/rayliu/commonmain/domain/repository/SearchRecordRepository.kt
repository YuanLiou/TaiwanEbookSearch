package com.rayliu.commonmain.domain.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.rayliu.commonmain.domain.SimpleResult
import com.rayliu.commonmain.domain.model.SearchRecord

interface SearchRecordRepository {
    fun getPagingSearchRecordsFactory(): LiveData<PagingData<SearchRecord>>
    suspend fun getSearchRecordsCounts(): SimpleResult<Int>
    suspend fun saveKeywordToLocal(keyword: String)
    suspend fun deleteRecords(searchRecord: SearchRecord)
}