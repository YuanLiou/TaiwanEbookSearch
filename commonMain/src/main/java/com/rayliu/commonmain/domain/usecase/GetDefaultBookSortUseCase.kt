package com.rayliu.commonmain.domain.usecase

import com.rayliu.commonmain.domain.repository.BookRepository

class GetDefaultBookSortUseCase(private val bookRepository: BookRepository) {
    operator fun invoke() = bookRepository.getDefaultResultSort()
}