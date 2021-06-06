package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.repository.SearchRecordRepository

class GetSearchRecordsUseCase(
    private val searchRecordRepository: SearchRecordRepository
) {
    operator fun invoke() = searchRecordRepository.getPagingSearchRecordsFactory()
}