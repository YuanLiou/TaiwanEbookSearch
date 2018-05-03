package liou.rayyuan.ebooksearchtaiwan.viewmodel

import liou.rayyuan.ebooksearchtaiwan.model.entity.Book

/**
 * Created by louis383 on 2017/12/4.
 */
class BookViewModel(private val book: Book) {

    fun getTitle(): String {
        return book.title
    }

    fun getDescription(): String {
        return book.about
    }

    fun getImage(): String {
        return book.thumbnail
    }

    fun getPrice(): String {
        if (book.priceCurrency == "TWD") {
            val price:Int = book.price.toInt()
            return "$" + price + " " + book.priceCurrency
        }

        return "$" + book.price + " " + book.priceCurrency
    }

    fun getShopName(): String {
        return book.bookStore ?: ""
    }
}