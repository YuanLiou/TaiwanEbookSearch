package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import liou.rayyuan.ebooksearchtaiwan.Presenter
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.model.ConnectionType.GET
import liou.rayyuan.ebooksearchtaiwan.model.EbookSearchService
import liou.rayyuan.ebooksearchtaiwan.model.NetworkConnectionListener
import liou.rayyuan.ebooksearchtaiwan.model.NetworkConnector
import liou.rayyuan.ebooksearchtaiwan.model.entity.Book
import liou.rayyuan.ebooksearchtaiwan.model.entity.BookStores
import liou.rayyuan.ebooksearchtaiwan.model.utils.BookStoresUtils
import liou.rayyuan.ebooksearchtaiwan.view.BookResultAdapter
import liou.rayyuan.ebooksearchtaiwan.view.BookResultClickHandler
import liou.rayyuan.ebooksearchtaiwan.view.ViewState.*

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
    lateinit var bookWalkerAdapter: BookResultAdapter
    lateinit var playBooksAdapter: BookResultAdapter

    private val maxListNumber: Int = 10
    private var eggCount: Int = 0

    override fun attachView(view: BookSearchView) {
        this.view = view
        ebookSearchService = EbookSearchService()
        view.setupInterface()
    }

    override fun detachView() {
        this.view = null
    }

    fun setBestResultRecyclerView(recyclerView: RecyclerView) {
        bestResultAdapter = BookResultAdapter(false, -1, this)
        recyclerView.adapter = bestResultAdapter
    }

    fun setBookCompanyRecyclerView(recyclerView: RecyclerView) {
        bookCompanyAdapter = BookResultAdapter(true, maxListNumber, this)
        recyclerView.adapter = bookCompanyAdapter
    }

    fun setReadmooRecyclerView(recyclerView: RecyclerView) {
        readmooAdapter = BookResultAdapter(true, maxListNumber, this)
        recyclerView.adapter = readmooAdapter
    }

    fun setKoboRecyclerView(recyclerView: RecyclerView) {
        koboAdapter = BookResultAdapter(true, maxListNumber, this)
        recyclerView.adapter = koboAdapter
    }

    fun setTaazeRecyclerView(recyclerView: RecyclerView) {
        taazeAdapter = BookResultAdapter(true, maxListNumber, this)
        recyclerView.adapter = taazeAdapter
    }

    fun setBookWalkerRecyclerView(recyclerView: RecyclerView) {
        bookWalkerAdapter = BookResultAdapter(true, maxListNumber, this)
        recyclerView.adapter = bookWalkerAdapter
    }

    fun setPlayBooksRecycylerView(recyclerView: RecyclerView) {
        playBooksAdapter = BookResultAdapter(true, maxListNumber, this)
        recyclerView.adapter = playBooksAdapter
    }

    fun hintPressed() {
        view?.showVirtualKeyboard()

        eggCount++
        if (eggCount == 10) {
            view?.showEasterEgg01()
        }
    }

    fun searchBook(keyword: String) {
        if (keyword.trim().isNotBlank()) {
            view?.hideVirtualKeyboard()
            if (view!!.isInternetConnectionAvailable()) {
                val queryString: HashMap<String, String> = HashMap()
                queryString.put("q", keyword)

                val connector = NetworkConnector(ebookSearchService, GET, this)
                val targetURL: String? = ebookSearchService.getBooksInfo(queryString)
                connector.execute(targetURL)
                view?.setMainResultView(PREPARE)
                resetCurrentResults()
            } else {
                view?.showInternetRequestDialog()
            }
        } else {
            view?.showKeywordIsEmpty()
        }
    }

    private fun resetCurrentResults() {
        bestResultAdapter.resetBooks()
        bookCompanyAdapter.resetBooks()
        readmooAdapter.resetBooks()
        koboAdapter.resetBooks()
        taazeAdapter.resetBooks()
        bookWalkerAdapter.resetBooks()
    }

    override fun onNetworkConnectionSucceed(result: String?) {
        val bookStores: BookStores? = result?.let { BookStoresUtils.convertJsonToBookStores(it) }
        if (bookStores != null) {
            view?.scrollToTop()

            bookStores.booksCompany?.let {
                if (it.isNotEmpty()) {
                    val bookStoreName = view?.getApplicationString(R.string.books_companyt_title)
                    bestResultAdapter.addBook(it.first(), bookStoreName!!)
                    bookCompanyAdapter.setBooks(it)
                } else {
                    view?.bookCompanyIsEmpty()
                }
            }

            bookStores.readmoo?.let {
                if (it.isNotEmpty()) {
                    val bookStoreName = view?.getApplicationString(R.string.readmoo_title)
                    bestResultAdapter.addBook(it.first(), bookStoreName!!)
                    readmooAdapter.setBooks(it)
                } else {
                    view?.readmooIsEmpty()
                }
            }

            bookStores.kobo?.let {
                if (it.isNotEmpty()) {
                    val bookStoreName = view?.getApplicationString(R.string.kobo_title)
                    bestResultAdapter.addBook(it.first(), bookStoreName!!)
                    koboAdapter.setBooks(it)
                } else {
                    view?.koboIsEmpty()
                }
            }

            bookStores.taaze?.let {
                if (it.isNotEmpty()) {
                    val bookStoreName = view?.getApplicationString(R.string.taaze_title)
                    bestResultAdapter.addBook(it.first(), bookStoreName!!)
                    taazeAdapter.setBooks(it)
                } else {
                    view?.taazeIsEmpty()
                }
            }

            bookStores.bookWalker?.let {
                if (it.isNotEmpty()) {
                    val bookStoreName = view?.getApplicationString(R.string.book_walker_title)
                    bestResultAdapter.addBook(it.first(), bookStoreName!!)
                    bookWalkerAdapter.setBooks(it)
                } else {
                    view?.bookWalkerIsEmpty()
                }
            }

            bookStores.playStore?.let {
                if (it.isNotEmpty()) {
                    val bookStoreName = view?.getApplicationString(R.string.playbook_title)
                    bestResultAdapter.addBook(it.first(), bookStoreName!!)
                    playBooksAdapter.setBooks(it)
                } else {
                    view?.playBookIsEmpty()
                }
            }

            bestResultAdapter.sortByMoney()
            view?.setMainResultView(READY)
        }
    }

    override fun onNetworkConnectionError(result: String?, responseCode: Int?) {
        view?.setMainResultView(ERROR)
        val message = "An error occurred\n $result\n Code: $responseCode"
        view?.showErrorMessage(message)
    }

    override fun onNetworkTimeout() {
        view?.showInternetConnectionTimeout()
    }

    override fun onBookCardClicked(book: Book) {
        view?.openBookLink(Uri.parse(book.link))
        Log.e("BookSearchPresenter", "Book Name: " + book.title)
    }
}