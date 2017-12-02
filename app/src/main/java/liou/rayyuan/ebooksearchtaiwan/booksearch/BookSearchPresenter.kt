package liou.rayyuan.ebooksearchtaiwan.booksearch

import liou.rayyuan.ebooksearchtaiwan.Presenter

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchPresenter : Presenter<BookSearchView> {

    private var view: BookSearchView? = null

    override fun attachView(view: BookSearchView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }
}