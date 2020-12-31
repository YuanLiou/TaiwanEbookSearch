package liou.rayyuan.ebooksearchtaiwan.model.data.mapper

import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkBook
import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkBookStores
import liou.rayyuan.ebooksearchtaiwan.model.data.mapper.basic.Mapper
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookStores
import liou.rayyuan.ebooksearchtaiwan.utils.DefaultStoreNames

class BookStoreDataMapper(
    private val bookDataMapper: BookDataMapper
) : Mapper<NetworkBookStores, BookStores> {

    override fun map(input: NetworkBookStores): BookStores {
        return with(input) {
            BookStores(
                booksCompany = booksCompany.convert(DefaultStoreNames.BOOK_COMPANY),
                readmoo = readmoo.convert(DefaultStoreNames.READMOO),
                kobo = kobo.convert(DefaultStoreNames.KOBO),
                taaze = taaze.convert(DefaultStoreNames.TAAZE),
                bookWalker = bookWalker.convert(DefaultStoreNames.BOOK_WALKER),
                playStore = playStore.convert(DefaultStoreNames.PLAY_STORE),
                pubu = pubu.convert(DefaultStoreNames.PUBU),
                hyread = hyread.convert(DefaultStoreNames.HYREAD)
            )
        }
    }

    private fun List<NetworkBook>?.convert(defaultBookStores: DefaultStoreNames): List<Book>? {
        return this?.mapIndexed { index, networkBook ->
            bookDataMapper.map(networkBook).apply {
                bookStore = defaultBookStores
                isFirstChoice = (index == 0)
            }
        }
    }
}