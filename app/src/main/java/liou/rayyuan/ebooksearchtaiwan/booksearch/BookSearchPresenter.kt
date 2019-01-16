package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import liou.rayyuan.ebooksearchtaiwan.Presenter
import liou.rayyuan.ebooksearchtaiwan.model.*
import liou.rayyuan.ebooksearchtaiwan.model.entity.AdapterItem
import liou.rayyuan.ebooksearchtaiwan.model.entity.Book
import liou.rayyuan.ebooksearchtaiwan.model.entity.BookHeader
import liou.rayyuan.ebooksearchtaiwan.model.entity.BookStores
import liou.rayyuan.ebooksearchtaiwan.utils.DefaultStoreNames
import liou.rayyuan.ebooksearchtaiwan.view.BookResultClickHandler
import liou.rayyuan.ebooksearchtaiwan.view.FullBookStoreResultAdapter
import liou.rayyuan.ebooksearchtaiwan.view.ViewState.*
import liou.rayyuan.ebooksearchtaiwan.viewmodel.BookListViewModel
import okhttp3.ResponseBody

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchPresenter(private val apiManager: APIManager,
                          private val preferenceManager: UserPreferenceManager,
                          private val eventTracker: EventTracker) : Presenter<BookSearchView>,
        LifecycleObserver, OnNetworkConnectionListener, BookResultClickHandler {

    private var view: BookSearchView? = null
    private lateinit var bookListViewModel: BookListViewModel

    private val dataSetJob = Job()
    private val backgroundScope = CoroutineScope(Dispatchers.IO + dataSetJob)

    private lateinit var fullBookStoreResultsAdapter: FullBookStoreResultAdapter
    private val maxListNumber: Int = 10
    private var eggCount: Int = 0
    private var bookStores: BookStores? = null
    private val defaultResultSort by lazy {
        preferenceManager.getBookStoreSort() ?: run {
            val defaultSort = listOf(DefaultStoreNames.READMOO,
                    DefaultStoreNames.KOBO,
                    DefaultStoreNames.BOOK_WALKER,
                    DefaultStoreNames.BOOK_COMPANY,
                    DefaultStoreNames.TAAZE,
                    DefaultStoreNames.PLAY_STORE,
                    DefaultStoreNames.PUBU,
                    DefaultStoreNames.HYREAD)
            preferenceManager.saveBookStoreSort(defaultSort)
            preferenceManager.getBookStoreSort()!!
        }
    }

    internal var lastScrollPosition: Int = 0

    override fun attachView(view: BookSearchView) {
        this.view = view
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun detachView() {
        if (dataSetJob.isActive && !dataSetJob.isCompleted) {
            dataSetJob.cancel()
        }
        this.view = null
    }

    internal fun ready() {
        view?.setupUI()
        view?.let { attachedView ->
            bookListViewModel = attachedView.getViewModelProvider().get(BookListViewModel::class.java)
            bookListViewModel.apiManager = apiManager
            bookListViewModel.networkConnectionListener = this
            if (bookListViewModel.isRequestingData()) {
                attachedView.setMainResultView(PREPARE)
            }

            val bookLiveData = bookListViewModel.getBookList(force = false)
            bookLiveData?.let { liveData ->
                liveData.listener = this
                // Restore ViewModels
                liveData.observe(attachedView.getLifeCycleOwner(), Observer {
                    prepareBookSearchResult(it)
                })
            }
            attachedView.getLifeCycleOwner().lifecycle.addObserver(this)
        }
    }

    fun hintPressed() {
        view?.focusBookSearchEditText()

        eggCount++
        if (eggCount == 10) {
            view?.showEasterEgg01()
            eventTracker.logEvent(EventTracker.SHOW_EASTER_EGG_01)
        }
        eventTracker.logEvent(EventTracker.CLICK_INFO_BUTTON)
    }

    fun backToTop(canResultListScrollVertically: Boolean) {
        if (canResultListScrollVertically) {
            view?.backToListTop()
            eventTracker.logEvent(EventTracker.CLICK_BACK_TO_TOP_BUTTON)
        } else {
            view?.focusBookSearchEditText()
            eventTracker.logEvent(EventTracker.CLICK_TO_SEARCH_BUTTON)
        }
    }

    fun searchBook(keyword: String) {
        if (!this::bookListViewModel.isInitialized) {
            return
        }

        view?.let {
            if (keyword.trim().isNotBlank()) {
                it.hideVirtualKeyboard()

                if (it.isInternetConnectionAvailable()) {

                    if (bookListViewModel.isRequestingData()) {
                        bookListViewModel.forceStop()
                    }

                    bookListViewModel.getBookList(keyword, true)
                            ?.observe(it.getLifeCycleOwner(), Observer { bookStores ->
                                prepareBookSearchResult(bookStores)
                            })

                    it.setMainResultView(PREPARE)
                    it.scrollToTop()
                    resetCurrentResults()
                } else {
                    it.showInternetRequestDialog()
                }

            } else {
                it.showKeywordIsEmpty()
            }
        }
    }

    private fun resetCurrentResults() {
        fullBookStoreResultsAdapter.clean()
    }

    fun setResultRecyclerView(recyclerView: RecyclerView) {
        fullBookStoreResultsAdapter = FullBookStoreResultAdapter(this, eventTracker)

        recyclerView.adapter = fullBookStoreResultsAdapter
    }

    private fun prepareBookSearchResult(bookStores: BookStores) {
        this.bookStores = bookStores

        backgroundScope.launch {
            val adapterItems = mutableListOf<AdapterItem>()
            val bookItems = bookStores.generateBookStoresResultMap(defaultResultSort)

            val bestItems = mutableListOf<Book>()
            bookItems.forEach{ (key, value) ->
                if (defaultResultSort.contains(key)) {
                    val book = value.firstOrNull()?.apply {
                        isFirstChoice = true
                        bookStore = key.defaultName
                    }

                    book?.run {
                        bestItems.add(this)
                    }
                }
            }
            bestItems.sortWith( compareBy { it.price })

            adapterItems.add(BookHeader(DefaultStoreNames.BEST_RESULT.defaultResId, bestItems.isEmpty()))
            adapterItems.addAll(bestItems)

            defaultResultSort.forEach {
                val books = bookItems[it]?.run {
                    drop(1)
                }?.run {
                    take(maxListNumber)
                }?.run {
                    sortedWith(compareBy { book -> book.price })
                }

                adapterItems.add(BookHeader(it.defaultResId, books?.isEmpty() ?: true))
                books?.let { resultList ->
                    resultList.forEach { book ->
                        book.bookStore = it.defaultName
                    }
                    adapterItems.addAll(resultList)
                }
            }

            withContext(Dispatchers.Main) {
                fullBookStoreResultsAdapter.addResult(adapterItems)

                if (lastScrollPosition > 0) {
                    view?.scrollToPosition(lastScrollPosition)
                    lastScrollPosition = 0
                }

                view?.setMainResultView(READY)
            }
        }
    }

    fun logISBNScanningSucceed() {
        eventTracker.logEvent(EventTracker.SCANNED_ISBN)
    }

    //region OnNetworkConnectionListener
    override fun onNetworkTimeout() {
        view?.showInternetConnectionTimeout()
    }

    override fun onNetworkErrorOccurred(errorBody: ResponseBody?) {
        view?.setMainResultView(ERROR)
        view?.showNetworkErrorMessage()
    }

    override fun onExceptionOccurred(message: String) {
        view?.setMainResultView(ERROR)
        if (message == NetworkLiveData.GENERIC_NETWORK_ISSUE) {
            view?.showNetworkErrorMessage()
        } else {
            view?.showToast(message)
        }
    }
    //endregion

    //region BookResultClickHandler
    override fun onBookCardClicked(book: Book) {
        view?.openBook(book)
    }
    //endregion
}