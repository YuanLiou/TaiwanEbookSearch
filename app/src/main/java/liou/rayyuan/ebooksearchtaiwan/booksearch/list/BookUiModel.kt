package liou.rayyuan.ebooksearchtaiwan.booksearch.list

import android.content.Context
import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.view.getLocalizedName

/**
 * Created by louis383 on 2017/12/4.
 */
data class BookUiModel(
    val book: Book
) : BookSearchResultItem {
    fun getTitle(): String = book.title

    fun getDescription(): String = book.about

    fun getImage(): String = book.thumbnail

    fun getPrice(): String {
        if (book.priceCurrency == "TWD") {
            var price: Int = book.price.toInt()
            if (price < 0) {
                price = 0
            }
            return "$" + price + " " + book.priceCurrency
        }

        return "$" + book.price + " " + book.priceCurrency
    }

    fun getShopName(context: Context): String =
        book.bookStore.let {
            DefaultStoreNames.entries
                .find { enumValues -> enumValues == it }
                ?.run { getLocalizedName(context) } ?: ""
        }

    fun getAuthors(context: Context): String? {
        val counts = book.authors?.size ?: 0
        if (counts > 0) {
            val result = book.authors?.joinToString(", ")
            if (result != null) {
                return context.resources.getQuantityString(R.plurals.title_authors, counts, result)
            }
        }
        return null
    }

    fun getLink(): String = book.link

    fun getShareText(): String = "${book.title} \n ${book.link}"
}

internal fun Book.asUiModel() = BookUiModel(this)
