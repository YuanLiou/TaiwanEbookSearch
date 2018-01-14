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
import liou.rayyuan.ebooksearchtaiwan.view.BookResultView
import liou.rayyuan.ebooksearchtaiwan.view.FullBookStoreResultAdapter
import liou.rayyuan.ebooksearchtaiwan.view.ViewState.*

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchPresenter : Presenter<BookSearchView>, NetworkConnectionListener, BookResultClickHandler {

    private var view: BookSearchView? = null

    lateinit var ebookSearchService: EbookSearchService
    lateinit var fullBookStoreResultsAdapter: FullBookStoreResultAdapter
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
        fullBookStoreResultsAdapter.clean()
    }

    fun setResultRecyclerView(recyclerView: RecyclerView) {
        fullBookStoreResultsAdapter = FullBookStoreResultAdapter(this)
        recyclerView.adapter = fullBookStoreResultsAdapter
    }

    override fun onNetworkConnectionSucceed(result: String?) {
        val bookStores: BookStores? = result?.let { BookStoresUtils.convertJsonToBookStores(it) }
        if (bookStores != null) {
            view?.scrollToTop()
            val bestResultsAdapter = BookResultAdapter(false, -1)

            bookStores.booksCompany?.let {
                val bookStoreName = view?.getApplicationString(R.string.books_companyt_title)
                addResult(bestResultsAdapter, bookStoreName!!, it)
            }

            bookStores.readmoo?.let {
                val bookStoreName = view?.getApplicationString(R.string.readmoo_title)
                addResult(bestResultsAdapter, bookStoreName!!, it)
            }

            bookStores.kobo?.let {
                val bookStoreName = view?.getApplicationString(R.string.kobo_title)
                addResult(bestResultsAdapter, bookStoreName!!, it)
            }

            bookStores.taaze?.let {
                val bookStoreName = view?.getApplicationString(R.string.taaze_title)
                addResult(bestResultsAdapter, bookStoreName!!, it)
            }

            bookStores.bookWalker?.let {
                val bookStoreName = view?.getApplicationString(R.string.book_walker_title)
                addResult(bestResultsAdapter, bookStoreName!!, it)
            }

            bookStores.playStore?.let {
                val bookStoreName = view?.getApplicationString(R.string.playbook_title)
                addResult(bestResultsAdapter, bookStoreName!!, it)
            }

            bookStores.pubu?.let {
                val bookStoreName = view?.getApplicationString(R.string.pubu_title)
                addResult(bestResultsAdapter, bookStoreName!!, it)
            }

            bestResultsAdapter.sortByMoney()

            val bestResultTitle = view?.getApplicationString(R.string.best_result_title)
            val bestResultView = BookResultView(bestResultTitle!!, bestResultsAdapter)
            fullBookStoreResultsAdapter.addResultToBeginning(bestResultView)

            view?.setMainResultView(READY)
        }
    }

    private fun addResult(bestAdapter: BookResultAdapter, bookStoreName: String, books: List<Book>) {
        if (books.isNotEmpty()) {
            bestAdapter.addBook(books.first(), bookStoreName)
        }
        fullBookStoreResultsAdapter.addResult(generateBookResultView(bookStoreName, maxListNumber, books))
    }

    private fun generateBookResultView(storeName: String, maxListNumber: Int, books: List<Book>): BookResultView {
        val bookResultAdapter = BookResultAdapter(true, maxListNumber)
        bookResultAdapter.setBooks(books)
        return BookResultView(storeName, bookResultAdapter)
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