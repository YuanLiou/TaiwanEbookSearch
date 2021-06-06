package liou.rayyuan.ebooksearchtaiwan.viewmodel

import android.content.Context
import com.rayliu.commonmain.domain.model.Book
import com.rayliu.commonmain.data.DefaultStoreNames
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.AdapterItem
import liou.rayyuan.ebooksearchtaiwan.view.getLocalizedName

/**
 * Created by louis383 on 2017/12/4.
 */
data class BookViewModel(val book: Book) : AdapterItem {

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
                ?.run { getLocalizedName(context)} ?: ""
    }
}