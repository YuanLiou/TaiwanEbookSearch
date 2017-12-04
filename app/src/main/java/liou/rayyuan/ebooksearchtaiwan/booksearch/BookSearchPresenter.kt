package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import liou.rayyuan.ebooksearchtaiwan.Presenter
import liou.rayyuan.ebooksearchtaiwan.model.ConnectionType.GET
import liou.rayyuan.ebooksearchtaiwan.model.EbookSearchService
import liou.rayyuan.ebooksearchtaiwan.model.NetworkConnectionListener
import liou.rayyuan.ebooksearchtaiwan.model.NetworkConnector
import liou.rayyuan.ebooksearchtaiwan.model.entity.Book
import liou.rayyuan.ebooksearchtaiwan.model.entity.BookStores
import liou.rayyuan.ebooksearchtaiwan.model.utils.BookStoresUtils
import liou.rayyuan.ebooksearchtaiwan.view.BookResultAdapter
import liou.rayyuan.ebooksearchtaiwan.view.BookResultClickHandler

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchPresenter : Presenter<BookSearchView>, NetworkConnectionListener, BookResultClickHandler {

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
        bestResultAdapter = BookResultAdapter(false, this)
        recyclerView.adapter = bestResultAdapter
    }

    fun setBookCompanyRecyclerView(recyclerView: RecyclerView) {
        bookCompanyAdapter = BookResultAdapter(true, this)
        recyclerView.adapter = bookCompanyAdapter
    }

    fun setReadmooRecyclerView(recyclerView: RecyclerView) {
        readmooAdapter = BookResultAdapter(true, this)
        recyclerView.adapter = readmooAdapter
    }

    fun setKoboRecyclerView(recyclerView: RecyclerView) {
        koboAdapter = BookResultAdapter(true, this)
        recyclerView.adapter = koboAdapter
    }

    fun setTaazeRecyclerView(recyclerView: RecyclerView) {
        taazeAdapter = BookResultAdapter(true, this)
        recyclerView.adapter = taazeAdapter
    }

    fun searchBook(keyword: String) {
        if (view!!.isInternetConnectionAvailable()) {
            val queryString: HashMap<String, String> = HashMap()
            queryString.put("q", keyword)

            val connector = NetworkConnector(ebookSearchService, GET, this)
            val targetURL: String? = ebookSearchService.getBooksInfo(queryString)
            connector.execute(targetURL)
            resetCurrentResults()
        } else {
            view?.showInternetRequestDialog()
        }
    }

    private fun resetCurrentResults() {
        bestResultAdapter.resetBooks()
        bookCompanyAdapter.resetBooks()
        readmooAdapter.resetBooks()
        koboAdapter.resetBooks()
        taazeAdapter.resetBooks()
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

    override fun onNetworkConnectionError(result: String?) {}

    override fun onNetworkTimeout() {
        view?.showInternetConnectionTimeout()
    }

    override fun onBookCardClicked(book: Book) {
        view?.openBookLink(Uri.parse(book.link))
        Log.e("BookSearchPresenter", "Book Name: " + book.title)
    }
}