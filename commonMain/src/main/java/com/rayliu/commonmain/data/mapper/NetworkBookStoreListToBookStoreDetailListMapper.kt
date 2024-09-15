package com.rayliu.commonmain.data.mapper

import com.rayliu.commonmain.data.dto.NetworkBookStore
import com.rayliu.commonmain.data.mapper.basic.NullableInputListMapper
import com.rayliu.commonmain.domain.model.BookStoreDetails

class NetworkBookStoreListToBookStoreDetailListMapper(
    private val mapper: BookStoreDetailsMapper
) : NullableInputListMapper<NetworkBookStore, BookStoreDetails> {
    override fun map(input: List<NetworkBookStore>?): List<BookStoreDetails> {
        if (input == null) {
            return emptyList()
        }
        return input.map { mapper.map(it) }
    }
}
