package liou.rayyuan.ebooksearchtaiwan.model.data.mapper

import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkBookStore
import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkResult
import liou.rayyuan.ebooksearchtaiwan.model.data.mapper.basic.NullableInputListMapper
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookStore

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