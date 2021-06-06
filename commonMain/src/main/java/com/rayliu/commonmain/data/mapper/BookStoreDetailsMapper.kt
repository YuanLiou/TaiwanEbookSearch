package com.rayliu.commonmain.data.mapper

import com.rayliu.commonmain.data.dto.NetworkBookStore
import com.rayliu.commonmain.data.mapper.basic.Mapper
import com.rayliu.commonmain.domain.model.BookStoreDetails

class BookStoreDetailsMapper : Mapper<NetworkBookStore, BookStoreDetails> {
    override fun map(input: NetworkBookStore): BookStoreDetails {
        return BookStoreDetails(
            isOnline = input.isOnline ?: false,
            displayName = input.displayName ?: "",
            status = input.status ?: "",
            id = input.id
        )
    }
}