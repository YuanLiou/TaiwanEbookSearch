package com.rayliu.commonmain.domain.usecase

import androidx.paging.PagingData
import com.rayliu.commonmain.domain.model.SearchRecord
import kotlinx.coroutines.flow.Flow

fun interface GetSearchRecordsUseCase : () -> Flow<PagingData<SearchRecord>>
