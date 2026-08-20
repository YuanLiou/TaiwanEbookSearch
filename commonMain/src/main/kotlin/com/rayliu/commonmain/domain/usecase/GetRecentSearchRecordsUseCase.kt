package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.model.RecentSearchRecord
import com.rayliu.commonmain.domain.repository.SearchRecordRepository

class GetRecentSearchRecordsUseCase(
    private val searchRecordRepository: SearchRecordRepository
) {
    companion object {
        const val DEFAULT_LIMIT = 10
        const val MAX_LIMIT = 20
    }

    suspend operator fun invoke(limit: Int = DEFAULT_LIMIT): List<RecentSearchRecord> {
        val resolvedLimit =
            when {
                limit <= 0 -> DEFAULT_LIMIT
                limit > MAX_LIMIT -> MAX_LIMIT
                else -> limit
            }
        return searchRecordRepository.getRecentSearchRecords(resolvedLimit)
    }
}
