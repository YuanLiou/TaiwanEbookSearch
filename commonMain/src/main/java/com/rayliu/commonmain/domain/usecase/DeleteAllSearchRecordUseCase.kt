package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.repository.SearchRecordRepository

class DeleteAllSearchRecordUseCase(
    private val searchRecordRepository: SearchRecordRepository
) {
    suspend operator fun invoke() {
        searchRecordRepository.deleteAllRecords()
    }
}
