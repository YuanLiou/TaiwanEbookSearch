package liou.rayyuan.ebooksearchtaiwan.uimodel

import android.content.Context
import com.rayliu.commonmain.domain.model.Book
import com.rayliu.commonmain.data.DefaultStoreNames
import java.util.regex.Pattern
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.AdapterItem
import liou.rayyuan.ebooksearchtaiwan.view.getLocalizedName

/**
 * Created by louis383 on 2017/12/4.
 */
data class BookUiModel(val book: Book) : AdapterItem {

    fun getTitle(): String {
        return book.title.removeSpaces()
    }

    fun getDescription(): String {
        return book.about.removeSpaces()
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

    private val chineseCharacterPattern = Pattern.compile("([\\u4E00-\\u9FFF]|([：？！]))\\s+([\\u4E00-\\u9FFF]|([：？！]))")
    private fun String.removeSpaces(): String {
        val trimmedString = this.trim()
        return chineseCharacterPattern.matcher(trimmedString).replaceAll("$1$2")
    }
}

internal fun Book.asUiModel() = BookUiModel(this)