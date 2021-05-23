package liou.rayyuan.ebooksearchtaiwan.model.data.mapper

import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkBookStore
import liou.rayyuan.ebooksearchtaiwan.model.data.mapper.basic.Mapper
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookStoreDetails

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