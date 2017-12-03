package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.app.Activity
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import liou.rayyuan.ebooksearchtaiwan.R

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchActivity : AppCompatActivity(), BookSearchView, View.OnClickListener {

    private val searchButton: ImageView by bindView(R.id.search_view_search_icon)
    private val searchEditText: EditText by bindView(R.id.search_view_edittext)

    private val bestResultTitle: TextView by bindView(R.id.search_result_subtitle_top)
    private val bestResultRecyclerView: RecyclerView by bindView(R.id.search_result_list_top)

    private val bookCompanyTitle: TextView by bindView(R.id.search_result_subtitle_store1)
    private val bookCompanyRecyclerView: RecyclerView by bindView(R.id.search_result_list_store1)

    private val readmooTitle: TextView by bindView(R.id.search_result_subtitle_store2)
    private val readmooRecyclerView: RecyclerView by bindView(R.id.search_result_list_store2)

    private val koboTitle: TextView by bindView(R.id.search_result_subtitle_store3)
    private val koboRecyclerView: RecyclerView by bindView(R.id.search_result_list_store3)

    private val taazeTitle: TextView by bindView(R.id.search_result_subtitle_store4)
    private val taazeRecyclerView: RecyclerView by bindView(R.id.search_result_list_store4)

    lateinit var presenter : BookSearchPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        presenter = BookSearchPresenter()
        presenter.attachView(this)
        presenter.setBestResultRecyclerView(bestResultRecyclerView)
        presenter.setBookCompanyRecyclerView(bookCompanyRecyclerView)
        presenter.setReadmooRecyclerView(readmooRecyclerView)
        presenter.setKoboRecyclerView(koboRecyclerView)
        presenter.setTaazeRecyclerView(taazeRecyclerView)

        searchButton.setOnClickListener(this)
    }

    override fun setupInterface() {
        bestResultTitle.text = resources.getString(R.string.best_result_title)
        bookCompanyTitle.text = resources.getString(R.string.booksCompanytTitle)
        readmooTitle.text = resources.getString(R.string.readmooTitle)
        koboTitle.text = resources.getString(R.string.koboTitle)
        taazeTitle.text = resources.getString(R.string.taazeTitle)
    }

    override fun bookCompanyIsEmpty() {
    }

    override fun readmooIsEmpty() {
    }

    override fun koboIsEmpty() {
    }

    override fun taazeIsEmpty() {
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.search_view_search_icon -> {
                val keyword: String = searchEditText.text.toString()
                presenter.searchBook(keyword)
            }
        }
    }

    private fun <T: View> Activity.bindView(@IdRes resId: Int): Lazy<T> {
        return lazy { findViewById<T>(resId) }
    }
}