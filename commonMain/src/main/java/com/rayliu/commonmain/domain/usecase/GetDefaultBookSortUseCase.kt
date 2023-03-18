package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.data.DefaultStoreNames
import kotlinx.coroutines.flow.Flow

fun interface GetDefaultBookSortUseCase : () -> Flow<List<DefaultStoreNames>>