package liou.rayyuan.ebooksearchtaiwan.uimodel

import android.content.Context
import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.AdapterItem
import liou.rayyuan.ebooksearchtaiwan.view.getLocalizedName

/**
 * Created by louis383 on 2017/12/4.
 */
data class BookUiModel(
    val book: Book
) : AdapterItem {
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

    companion object {
        internal val DUMMY_DATA
            get() =
                BookUiModel(
                    Book(
                        thumbnail = "",
                        priceCurrency = "TWD",
                        price = 1000.0f,
                        link = "https://www.google.com.tw",
                        about = "Our fraternal intuition for freedom is to forget others purely.",
                        id = "-1",
                        title = "Lucia Khatun",
                        authors = listOf("Dilip Suarez"),
                        bookStore = DefaultStoreNames.KINDLE,
                        isFirstChoice = false,
                        titleKeywordSimilarity = null
                    )
                )
    }
}

internal fun Book.asUiModel() = BookUiModel(this)
