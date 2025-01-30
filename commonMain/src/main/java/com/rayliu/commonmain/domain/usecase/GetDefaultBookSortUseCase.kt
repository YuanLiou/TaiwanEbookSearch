package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.data.DefaultStoreNames
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

fun interface GetDefaultBookSortUseCase : () -> Flow<ImmutableList<DefaultStoreNames>>
