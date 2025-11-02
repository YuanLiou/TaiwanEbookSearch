package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.model.SearchRecord
import com.rayliu.commonmain.domain.repository.SearchRecordRepository

class DeleteSearchRecordUseCase(
    private val searchRecordRepository: SearchRecordRepository
) {
    suspend operator fun invoke(searchRecord: SearchRecord) {
        searchRecordRepository.deleteRecords(searchRecord)
    }
}
