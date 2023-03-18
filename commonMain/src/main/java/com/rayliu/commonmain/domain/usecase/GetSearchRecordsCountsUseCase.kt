package com.rayliu.commonmain.domain.usecase

fun interface GetSearchRecordsCountsUseCase : suspend () -> Result<Int>