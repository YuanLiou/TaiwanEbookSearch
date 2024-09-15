package com.rayliu.commonmain.data.mapper

import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.data.dto.NetworkBookStore
import com.rayliu.commonmain.data.mapper.basic.Mapper
import com.rayliu.commonmain.domain.model.BookStoreDetails

class BookStoreDetailsMapper : Mapper<NetworkBookStore, BookStoreDetails> {
    private var enableStores: List<DefaultStoreNames> = emptyList()

    fun setEnableStores(enableStores: List<DefaultStoreNames>) {
        this.enableStores = enableStores
    }

    override fun map(input: NetworkBookStore): BookStoreDetails =
        BookStoreDetails(
            isOnline = input.isOnline ?: false,
            displayName = input.displayName.orEmpty(),
            status = input.status.orEmpty(),
            id = input.id,
            url = input.website.orEmpty(),
            isEnable = enableStores.contains(DefaultStoreNames.fromName(input.id))
        )
}
