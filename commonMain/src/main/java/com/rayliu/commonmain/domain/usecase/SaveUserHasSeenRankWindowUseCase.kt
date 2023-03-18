package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.repository.BrowseHistoryRepository
import kotlinx.coroutines.flow.firstOrNull

class SaveUserHasSeenRankWindowUseCase(
    private val browseHistoryRepository: BrowseHistoryRepository
) {
    suspend operator fun invoke() = browseHistoryRepository.setUserHasSeenRankWindow()
}