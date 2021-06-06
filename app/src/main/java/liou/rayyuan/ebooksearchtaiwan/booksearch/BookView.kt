package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.content.Context
import liou.rayyuan.ebooksearchtaiwan.R
import com.rayliu.commonmain.domain.model.Book

class BookView(val book: Book) {

    fun getAuthors(context: Context): String? {
        val counts = book.authors?.size ?: 0
        if (counts > 0) {
            val result = book.authors?.joinToString(", ")
            result?.run {
                return context.resources.getQuantityString(R.plurals.title_authors, counts, this)
            }
        }
        return null
    }

}