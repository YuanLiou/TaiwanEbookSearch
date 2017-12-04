package liou.rayyuan.ebooksearchtaiwan.view

import liou.rayyuan.ebooksearchtaiwan.model.entity.Book

/**
 * Created by louis383 on 2017/12/4.
 */
interface BookResultClickHandler {
    fun onBookCardClicked(book: Book)
}