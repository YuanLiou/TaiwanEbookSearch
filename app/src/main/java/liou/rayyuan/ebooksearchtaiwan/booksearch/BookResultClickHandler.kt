package liou.rayyuan.ebooksearchtaiwan.booksearch

import com.rayliu.commonmain.domain.model.Book

/**
 * Created by louis383 on 2017/12/4.
 */
interface BookResultClickHandler {
    fun onBookCardClicked(book: Book)
}
