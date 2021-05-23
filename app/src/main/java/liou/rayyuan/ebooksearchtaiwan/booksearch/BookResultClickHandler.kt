package liou.rayyuan.ebooksearchtaiwan.booksearch

import liou.rayyuan.ebooksearchtaiwan.model.domain.model.Book

/**
 * Created by louis383 on 2017/12/4.
 */
interface BookResultClickHandler {
    fun onBookCardClicked(book: Book)
}