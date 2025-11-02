package com.rayliu.commonmain.domain.repository

import kotlinx.coroutines.flow.Flow

interface BrowseHistoryRepository {
    suspend fun isUserSeenRankWindow(): Flow<Boolean>

    suspend fun setUserHasSeenRankWindow()
}
