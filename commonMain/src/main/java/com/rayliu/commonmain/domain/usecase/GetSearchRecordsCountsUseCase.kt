package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.repository.SearchRecordRepository

class GetSearchRecordsCountsUseCase(
    private val searchRecordRepository: SearchRecordRepository
) {
    suspend operator fun invoke(): Result<Int> {
        return searchRecordRepository.getSearchRecordsCounts()
    }
}