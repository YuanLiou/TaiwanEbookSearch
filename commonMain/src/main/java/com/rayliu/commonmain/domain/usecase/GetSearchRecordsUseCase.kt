package com.rayliu.commonmain.domain.usecase

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.rayliu.commonmain.domain.model.SearchRecord

fun interface GetSearchRecordsUseCase : () -> LiveData<PagingData<SearchRecord>>