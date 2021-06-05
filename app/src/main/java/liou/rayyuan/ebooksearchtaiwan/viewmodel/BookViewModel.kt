package liou.rayyuan.ebooksearchtaiwan.viewmodel

import android.content.Context
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.utils.DefaultStoreNames
import liou.rayyuan.ebooksearchtaiwan.utils.getLocalizedName

/**
 * Created by louis383 on 2017/12/4.
 */
class BookViewModel(val book: Book) {

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
            val price: Int = book.price.toInt()
            return "$" + price + " " + book.priceCurrency
        }

        return "$" + book.price + " " + book.priceCurrency
    }

    fun getShopName(context: Context): String = book.bookStore.let {
        DefaultStoreNames.values()
                .find { enumValues -> enumValues == it }
                ?.run { getLocalizedName(context )} ?: ""
    }
}