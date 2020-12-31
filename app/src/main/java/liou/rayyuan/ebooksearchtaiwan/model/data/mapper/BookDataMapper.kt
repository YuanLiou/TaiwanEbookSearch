package liou.rayyuan.ebooksearchtaiwan.model.data.mapper

import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkBook
import liou.rayyuan.ebooksearchtaiwan.model.data.mapper.basic.Mapper
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.Book

class BookDataMapper : Mapper<NetworkBook, Book> {
    override fun map(input: NetworkBook): Book {
        return with(input) {
            Book(
                thumbnail = thumbnail ?: "",
                priceCurrency = priceCurrency ?: "TWD",
                price = price ?: 0.0f,
                link = link ?: "",
                about = about ?: "",
                id = id ?: "",
                title = title ?: "",
                authors = authors.orEmpty(),
                bookStore = null,
                isFirstChoice = false
            )
        }
    }
}