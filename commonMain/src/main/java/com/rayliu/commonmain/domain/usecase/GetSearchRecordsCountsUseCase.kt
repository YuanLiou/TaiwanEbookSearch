package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.Result
import com.rayliu.commonmain.domain.SimpleResult
import com.rayliu.commonmain.domain.repository.SearchRecordRepository

class GetSearchRecordsCountsUseCase(
    private val searchRecordRepository: SearchRecordRepository
) {
    suspend operator fun invoke(): SimpleResult<Int> {
        return when (val response = searchRecordRepository.getSearchRecordsCounts()) {
            is Result.Success -> Result.Success(response.value)
            is Result.Failed -> Result.Failed(IllegalStateException("get search records job is failed."))
        }
    }
}