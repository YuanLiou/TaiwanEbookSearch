package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import liou.rayyuan.ebooksearchtaiwan.R

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchActivity : AppCompatActivity(), BookSearchView {

    var presenter : BookSearchPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        presenter = BookSearchPresenter()
        presenter!!.attachView(this)
    }
}