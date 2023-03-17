package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.TaskResult
import com.rayliu.commonmain.domain.SimpleResult
import com.rayliu.commonmain.domain.repository.SearchRecordRepository

class GetSearchRecordsCountsUseCase(
    private val searchRecordRepository: SearchRecordRepository
) {
    suspend operator fun invoke(): SimpleResult<Int> {
        return when (val response = searchRecordRepository.getSearchRecordsCounts()) {
            is TaskResult.Success -> TaskResult.Success(response.value)
            is TaskResult.Failed -> TaskResult.Failed(IllegalStateException("get search records job is failed."))
        }
    }
}