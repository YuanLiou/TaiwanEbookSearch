package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.repository.BookRepository

class SaveDefaultBookSortUseCase(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(currentSort: List<DefaultStoreNames>) = bookRepository.saveDefaultResultSort(currentSort)
}