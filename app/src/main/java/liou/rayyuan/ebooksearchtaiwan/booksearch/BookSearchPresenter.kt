package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.Observer
import android.net.Uri
import android.support.v7.widget.RecyclerView
import liou.rayyuan.ebooksearchtaiwan.Presenter
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.model.APIManager
import liou.rayyuan.ebooksearchtaiwan.model.OnNetworkConnectionListener
import liou.rayyuan.ebooksearchtaiwan.model.entity.Book
import liou.rayyuan.ebooksearchtaiwan.model.entity.BookStores
import liou.rayyuan.ebooksearchtaiwan.view.BookResultAdapter
import liou.rayyuan.ebooksearchtaiwan.view.BookResultClickHandler
import liou.rayyuan.ebooksearchtaiwan.view.BookResultView
import liou.rayyuan.ebooksearchtaiwan.view.FullBookStoreResultAdapter
import liou.rayyuan.ebooksearchtaiwan.view.ViewState.*
import liou.rayyuan.ebooksearchtaiwan.viewmodel.BookListViewModel
import okhttp3.ResponseBody

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchPresenter : Presenter<BookSearchView>, LifecycleObserver,
    OnNetworkConnectionListener, BookResultClickHandler {

    private var view: BookSearchView? = null
    private var bookListViewModel: BookListViewModel? = null

    private lateinit var fullBookStoreResultsAdapter: FullBookStoreResultAdapter
    private val maxListNumber: Int = 10
    private var eggCount: Int = 0
    private val apiManager: APIManager = APIManager()
    private var bookStores: BookStores? = null

    override fun attachView(view: BookSearchView) {
        this.view = view
        view.setupUI()

        bookListViewModel = view.getViewModelProvider().get(BookListViewModel::class.java)
        bookListViewModel?.apiManager = apiManager

        val isRequestingData: Boolean = bookListViewModel?.isRequestingData() ?: false
        if (isRequestingData) {
            view.setMainResultView(PREPARE)
        }

        val bookLiveData = bookListViewModel?.getBookList("", false)
        bookLiveData?.listener = this
        // Restore ViewModels
        bookLiveData?.observe(view.getLifeCycleOwner(), Observer {
            onBookSearchSucceed(it)
        })

        view.getLifeCycleOwner().lifecycle.addObserver(this)
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
                bookListViewModel?.getBookList(keyword, true)
                    ?.observe(view!!.getLifeCycleOwner() , Observer {
                    onBookSearchSucceed(it)
                })
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

    private fun onBookSearchSucceed(bookStores: BookStores?) {
        this.bookStores = bookStores

        view?.scrollToTop()
        val bestResultsAdapter = BookResultAdapter(false, -1)

        this.bookStores?.booksCompany?.let {
            val bookStoreName = view?.getApplicationString(R.string.books_companyt_title)
            addResult(bestResultsAdapter, bookStoreName!!, it)
        }

        this.bookStores?.readmoo?.let {
            val bookStoreName = view?.getApplicationString(R.string.readmoo_title)
            addResult(bestResultsAdapter, bookStoreName!!, it)
        }

        this.bookStores?.kobo?.let {
            val bookStoreName = view?.getApplicationString(R.string.kobo_title)
            addResult(bestResultsAdapter, bookStoreName!!, it)
        }

        this.bookStores?.taaze?.let {
            val bookStoreName = view?.getApplicationString(R.string.taaze_title)
            addResult(bestResultsAdapter, bookStoreName!!, it)
        }

        this.bookStores?.bookWalker?.let {
            val bookStoreName = view?.getApplicationString(R.string.book_walker_title)
            addResult(bestResultsAdapter, bookStoreName!!, it)
        }

        this.bookStores?.playStore?.let {
            val bookStoreName = view?.getApplicationString(R.string.playbook_title)
            addResult(bestResultsAdapter, bookStoreName!!, it)
        }

        this.bookStores?.pubu?.let {
            val bookStoreName = view?.getApplicationString(R.string.pubu_title)
            addResult(bestResultsAdapter, bookStoreName!!, it)
        }

        bestResultsAdapter.sortByMoney()

        val bestResultTitle = view?.getApplicationString(R.string.best_result_title)
        val bestResultView = BookResultView(bestResultTitle!!, bestResultsAdapter)
        fullBookStoreResultsAdapter.addResultToBeginning(bestResultView)

        view?.setMainResultView(READY)
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

    override fun onNetworkTimeout() {
        view?.showInternetConnectionTimeout()
    }

    override fun onNetworkErrorOccurred(errorBody: ResponseBody?) {
        view?.setMainResultView(ERROR)
        view?.showNetworkErrorMessage()
    }

    override fun onBookCardClicked(book: Book) {
        view?.openBookLink(Uri.parse(book.link))
    }
}