package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.content.Context
import com.rayliu.commonmain.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.R

class BookView(val book: Book) {

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
}
