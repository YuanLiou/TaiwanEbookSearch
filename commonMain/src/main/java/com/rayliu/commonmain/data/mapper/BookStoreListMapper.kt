package com.rayliu.commonmain.data.mapper

import com.rayliu.commonmain.data.dto.NetworkResult
import com.rayliu.commonmain.data.mapper.basic.NullableInputListMapper
import com.rayliu.commonmain.domain.model.BookStore

class BookStoreListMapper(
    private val bookStoreMapper: BookStoreMapper
) : NullableInputListMapper<NetworkResult, BookStore> {
    override fun map(input: List<NetworkResult>?): List<BookStore> {
        if (input == null) {
            return emptyList()
        }
        return input.map { bookStoreMapper.map(it) }
    }
}