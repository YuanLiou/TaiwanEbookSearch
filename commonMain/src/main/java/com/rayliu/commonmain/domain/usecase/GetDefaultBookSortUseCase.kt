package com.rayliu.commonmain.domain.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.repository.BookRepository

class GetDefaultBookSortUseCase(private val bookRepository: BookRepository) {
    operator fun invoke() = bookRepository.getDefaultResultSort()

    fun getAsLiveData(): LiveData<List<DefaultStoreNames>> {
        return bookRepository.getDefaultResultSort().asLiveData()
    }
}