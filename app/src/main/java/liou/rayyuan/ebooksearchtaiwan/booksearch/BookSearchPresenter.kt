package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.support.v7.widget.RecyclerView
import liou.rayyuan.ebooksearchtaiwan.Presenter
import liou.rayyuan.ebooksearchtaiwan.model.ConnectionType.GET
import liou.rayyuan.ebooksearchtaiwan.model.EbookSearchService
import liou.rayyuan.ebooksearchtaiwan.model.NetworkConnectionListener
import liou.rayyuan.ebooksearchtaiwan.model.NetworkConnector
import liou.rayyuan.ebooksearchtaiwan.model.entity.BookStores
import liou.rayyuan.ebooksearchtaiwan.model.utils.BookStoresUtils
import liou.rayyuan.ebooksearchtaiwan.view.BookResultAdapter

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchPresenter : Presenter<BookSearchView>, NetworkConnectionListener {

    private var view: BookSearchView? = null

    lateinit var ebookSearchService: EbookSearchService
    lateinit var bestResultAdapter: BookResultAdapter
    lateinit var bookCompanyAdapter: BookResultAdapter
    lateinit var readmooAdapter: BookResultAdapter
    lateinit var koboAdapter: BookResultAdapter
    lateinit var taazeAdapter: BookResultAdapter

    override fun attachView(view: BookSearchView) {
        this.view = view
        ebookSearchService = EbookSearchService()
        view.setupInterface()
    }

    override fun detachView() {
        this.view = null
    }

    fun setBestResultRecyclerView(recyclerView: RecyclerView) {
        bestResultAdapter = BookResultAdapter(false)
        recyclerView.adapter = bestResultAdapter
    }

    fun setBookCompanyRecyclerView(recyclerView: RecyclerView) {
        bookCompanyAdapter = BookResultAdapter(true)
        recyclerView.adapter = bookCompanyAdapter
    }

    fun setReadmooRecyclerView(recyclerView: RecyclerView) {
        readmooAdapter = BookResultAdapter(true)
        recyclerView.adapter = readmooAdapter
    }

    fun setKoboRecyclerView(recyclerView: RecyclerView) {
        koboAdapter = BookResultAdapter(true)
        recyclerView.adapter = koboAdapter
    }

    fun setTaazeRecyclerView(recyclerView: RecyclerView) {
        taazeAdapter = BookResultAdapter(true)
        recyclerView.adapter = taazeAdapter
    }

    fun searchBook(keyword: String) {
        val queryString: HashMap<String, String> = HashMap()
        queryString.put("q", keyword)

        val connector = NetworkConnector(ebookSearchService, GET, this)
        val targetURL: String? = ebookSearchService.getBooksInfo(queryString)
        connector.execute(targetURL)

//        Log.i("BookSearchPresenter", "Search Button clicked!")
    }

    override fun onNetworkConnectionSucceed(result: String?) {
        val bookStores: BookStores? = result?.let { BookStoresUtils.convertJsonToBookStores(it) }
        if (bookStores != null) {
            bookStores.booksCompany?.let {
                if (it.isNotEmpty()) {
                    bookCompanyAdapter.setBooks(it)
                } else {
                    view?.bookCompanyIsEmpty()
                }
            }

            bookStores.readmoo?.let {
                if (it.isNotEmpty()) {
                    readmooAdapter.setBooks(it)
                } else {
                    view?.readmooIsEmpty()
                }
            }

            bookStores.kobo?.let {
                if (it.isNotEmpty()) {
                    koboAdapter.setBooks(it)
                } else {
                    view?.koboIsEmpty()
                }
            }

            bookStores.taaze?.let {
                if (it.isNotEmpty()) {
                    taazeAdapter.setBooks(it)
                } else {
                    view?.taazeIsEmpty()
                }
            }
        }
    }

    override fun onNetworkConnectionError(result: String?) {
    }

    override fun onNetworkTimeout() {
    }
}