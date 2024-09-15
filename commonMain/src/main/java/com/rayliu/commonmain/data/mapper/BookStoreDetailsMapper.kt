package com.rayliu.commonmain.data.mapper

import com.rayliu.commonmain.data.dto.NetworkBookStore
import com.rayliu.commonmain.data.mapper.basic.Mapper
import com.rayliu.commonmain.domain.model.BookStoreDetails

class BookStoreDetailsMapper : Mapper<NetworkBookStore, BookStoreDetails> {
    override fun map(input: NetworkBookStore): BookStoreDetails =
        BookStoreDetails(
            isOnline = input.isOnline ?: false,
            displayName = input.displayName.orEmpty(),
            status = input.status.orEmpty(),
            id = input.id,
            url = input.website.orEmpty()
        )
}
