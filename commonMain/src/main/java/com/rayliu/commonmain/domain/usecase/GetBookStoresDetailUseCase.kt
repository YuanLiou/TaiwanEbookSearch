package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.model.BookStoreDetails
import kotlinx.collections.immutable.ImmutableList

fun interface GetBookStoresDetailUseCase : suspend () -> Result<ImmutableList<BookStoreDetails>>
