package com.rayliu.commonmain.domain.usecase

import kotlinx.coroutines.flow.Flow

fun interface GetIsUserSeenRankWindowUseCase : suspend () -> Flow<Boolean>
