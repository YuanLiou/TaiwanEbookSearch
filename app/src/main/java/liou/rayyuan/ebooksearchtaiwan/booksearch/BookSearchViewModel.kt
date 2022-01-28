package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.Result
import com.rayliu.commonmain.domain.SimpleResult
import com.rayliu.commonmain.domain.model.BookResult
import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.domain.model.SearchRecord
import com.rayliu.commonmain.domain.usecase.DeleteSearchRecordUseCase
import com.rayliu.commonmain.domain.usecase.GetBooksWithStoresUseCase
import com.rayliu.commonmain.domain.usecase.GetDefaultBookSortUseCase
import com.rayliu.commonmain.domain.usecase.GetSearchRecordsCountsUseCase
import com.rayliu.commonmain.domain.usecase.GetSearchRecordsUseCase
import com.rayliu.commonmain.domain.usecase.GetSearchSnapshotUseCase
import com.rayliu.commonmain.generateBookStoresResultMap
import java.net.SocketTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.arch.IModel
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.AdapterItem
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.BookHeader
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.SiteInfo
import liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate.BookResultViewState
import liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate.ScreenState
import liou.rayyuan.ebooksearchtaiwan.model.EventTracker
import liou.rayyuan.ebooksearchtaiwan.utils.QuickChecker
import liou.rayyuan.ebooksearchtaiwan.utils.ResourceHelper
import liou.rayyuan.ebooksearchtaiwan.view.ViewEffect
import liou.rayyuan.ebooksearchtaiwan.view.getStringResource
import liou.rayyuan.ebooksearchtaiwan.viewmodel.BookViewModel

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchViewModel(
    private val getBooksWithStoresUseCase: GetBooksWithStoresUseCase,
    private val getSearchRecordsUseCase: GetSearchRecordsUseCase,
    private val getSearchRecordsCountsUseCase: GetSearchRecordsCountsUseCase,
    private val getDefaultBookSortUseCase: GetDefaultBookSortUseCase,
    private val getSearchSnapshotUseCase: GetSearchSnapshotUseCase,
    private val eventTracker: EventTracker,
    private val quickChecker: QuickChecker,
    private val deleteSearchRecordUseCase: DeleteSearchRecordUseCase,
    private val resourceHelper: ResourceHelper
) : ViewModel(),
    IModel<BookResultViewState, BookSearchUserIntent> {
    companion object {
        const val NO_MESSAGE = -1
        const val GENERIC_NETWORK_ISSUE = "generic-network-issue"
    }

    override val userIntents: Channel<BookSearchUserIntent> = Channel(Channel.UNLIMITED)
    private val _bookResultViewState = MutableLiveData<BookResultViewState>()
    override val viewState: LiveData<BookResultViewState>
        get() = _bookResultViewState

    private val _screenViewState = MutableLiveData<ViewEffect<ScreenState>>()
    val screenViewState: LiveData<ViewEffect<ScreenState>>
        get() = _screenViewState

    val searchRecordLiveData by lazy {
        getSearchRecordsUseCase()
    }

    private var networkJob: Job? = null
    private val maxListNumber: Int = 10
    private var eggCount: Int = 0
    private var bookStores: BookStores? = null
    private var previousKeyword: String? = null
    val hasPreviousSearch: Boolean
        get() = bookStores != null

    private var lastScrollPosition: Int = 0
        set(value) {
            field = if (!isRequestingBookData()) {
                value
            } else {
                0
            }
        }

    init {
        setupUserIntentHanding()
    }

    private fun setupUserIntentHanding() {
        viewModelScope.launch {
            userIntents.consumeAsFlow().collect {
                when (it) {
                    is BookSearchUserIntent.DeleteSearchRecord -> {
                        deleteRecords(it.searchRecord)
                    }
                    is BookSearchUserIntent.FocusOnTextEditing -> {
                        focusOnEditText(it.isFocus)
                    }
                    BookSearchUserIntent.OnViewReadyToServe -> {
                        ready()
                    }
                    BookSearchUserIntent.PressHint -> {
                        hintPressed()
                    }
                    is BookSearchUserIntent.SearchBook -> {
                        searchBook(it.keywords)
                    }
                    is BookSearchUserIntent.ShowSearchSnapshot -> {
                        requestSearchSnapshot(it.searchId)
                    }
                    BookSearchUserIntent.ShareSnapshot -> {
                        shareCurrentSnapshot()
                    }
                }
            }
        }
    }

    override fun onCleared() {
        forceStopRequestingBookData()
        eggCount = 0
        super.onCleared()
    }

    fun savePreviousScrollPosition(position: Int) {
        lastScrollPosition = position
    }

    private fun ready() {
        if (isRequestingBookData()) {
            updateScreen(BookResultViewState.PrepareBookResult())
        } else {
            bookStores?.let {
                prepareBookSearchResult(it)
            }
        }
    }

    private fun hintPressed() {
        eggCount++
        if (eggCount == 10) {
            sendViewEffect(ScreenState.EasterEgg)
            eventTracker.logEvent(EventTracker.SHOW_EASTER_EGG_01)
        }
        eventTracker.logEvent(EventTracker.CLICK_INFO_BUTTON)
    }

    private fun focusOnEditText(isFocus: Boolean) {
        if (isFocus) {
            viewModelScope.launch {
                getSearchRecordsCountsUseCase().fold(
                    success = { recordCounts ->
                        if (recordCounts > 0) {
                            updateScreen(BookResultViewState.ShowSearchRecordList(recordCounts))
                        } else {
                            updateScreen(BookResultViewState.HideSearchRecordList)
                        }
                    },
                    failure = {
                        updateScreen(BookResultViewState.HideSearchRecordList)
                    }
                )
            }
        } else {
            updateScreen(BookResultViewState.HideSearchRecordList)
        }
    }

    private fun searchBook(keyword: String) {
        if (keyword.trim().isBlank()) {
            sendViewEffect(ScreenState.EmptyKeyword)
            return
        }

        if (keyword == previousKeyword || keyword == bookStores?.searchKeyword) {
            Log.i("BookSearchViewModel", "search blocked")
            return
        }
        fetchBookResult(SearchBookAction(keyword))
    }

    private inner class SearchBookAction(private val keyword: String) :
        suspend () -> SimpleResult<BookStores> {
        override suspend fun invoke(): SimpleResult<BookStores> {
            this@BookSearchViewModel.previousKeyword = keyword
            val defaultSort = getDefaultBookSortUseCase().first()
            return getBooksWithStoresUseCase(defaultSort, keyword)
        }
    }

    private fun fetchBookResult(action: suspend () -> SimpleResult<BookStores>) {
        if (!quickChecker.isInternetConnectionAvailable()) {
            sendViewEffect(ScreenState.NoInternetConnection)
            return
        }

        if (isRequestingBookData()) {
            forceStopRequestingBookData()
        }

        updateScreen(BookResultViewState.PrepareBookResult(true))
        bookStores = null // clean up
        networkJob = viewModelScope.launch(Dispatchers.IO) {
            val response = action.invoke()
            withContext(Dispatchers.Main) {
                when (response) {
                    is Result.Success -> {
                        networkRequestSuccess(response.value)
                    }
                    is Result.Failed -> {
                        // ServerResponseException == internal server error
                        // ClientRequestException == response.status.value to get response code
                        // RedirectResponseException
                        this@BookSearchViewModel.previousKeyword = null
                        if (response.error is SocketTimeoutException) {
                            networkTimeout()
                        } else {
                            val message = response.error.localizedMessage ?: GENERIC_NETWORK_ISSUE
                            networkExceptionOccurred(message)
                        }
                    }
                }
            }
        }
        updateScreen(BookResultViewState.HideSearchRecordList)
    }

    private inner class ShowSearchSnapshotAction(private val searchId: String) :
        suspend () -> SimpleResult<BookStores> {
        override suspend fun invoke(): SimpleResult<BookStores> {
            this@BookSearchViewModel.previousKeyword = null
            return getSearchSnapshotUseCase(searchId)
        }
    }

    private fun requestSearchSnapshot(searchId: String) {
        if (searchId.isEmpty()) {
            sendViewEffect(ScreenState.EmptyKeyword)
            return
        }
        fetchBookResult(ShowSearchSnapshotAction(searchId))
    }

    private fun deleteRecords(searchRecord: SearchRecord) {
        viewModelScope.launch {
            deleteSearchRecordUseCase(searchRecord)
        }
    }

    private fun prepareBookSearchResult(bookStores: BookStores) {
        this.bookStores = bookStores

        viewModelScope.launch {
            val adapterItems = generateAdapterItems(bookStores)
            updateScreen(
                BookResultViewState.ShowBooks(
                    bookStores.searchKeyword,
                    lastScrollPosition,
                    adapterItems
                )
            )
            lastScrollPosition = 0
        }
    }

    private suspend fun generateAdapterItems(bookStores: BookStores) =
        withContext(Dispatchers.Default) {
            val adapterItems = mutableListOf<AdapterItem>()
            val defaultSort = getDefaultBookSortUseCase().first()
            val groupedResults = bookStores.generateBookStoresResultMap(defaultSort)

            val bestItems = generateBestItems(defaultSort, groupedResults)
            adapterItems.add(
                BookHeader(
                    DefaultStoreNames.BEST_RESULT.getStringResource(),
                    bestItems.isEmpty(),
                    siteInfo = null
                )
            )
            adapterItems.addAll(bestItems)

            for (storeName in defaultSort) {
                val bookResult = groupedResults[storeName] ?: continue
                val books = bookResult.books.run {
                    drop(1)
                }.run {
                    take(maxListNumber)
                }.run {
                    sortedWith(compareBy { book -> book.price })
                }

                adapterItems.add(
                    BookHeader(
                        storeName.getStringResource(),
                        books.isEmpty(),
                        siteInfo = SiteInfo(
                            isOnline = bookResult.isOnline,
                            isResultOkay = bookResult.isOkay,
                            status = bookResult.status
                        ),
                    )
                )

                adapterItems.addAll(
                    books.map { BookViewModel(it) }
                )
            }

            adapterItems.toList()
        }

    private fun generateBestItems(
        defaultSort: List<DefaultStoreNames>,
        bookItems: Map<DefaultStoreNames, BookResult>
    ): List<BookViewModel> {
        val bestItems = mutableListOf<BookViewModel>()
        bookItems.forEach { (key, value) ->
            if (defaultSort.contains(key)) {
                val book = value.books.firstOrNull()
                book?.let { currentBook ->
                    currentBook.isFirstChoice = true
                    bestItems.add(BookViewModel(currentBook))
                }
            }
        }
        bestItems.sortWith(compareBy { it.book.price })
        return bestItems
    }

    private fun shareCurrentSnapshot() {
        val currentBookStore = bookStores ?: run {
            sendViewEffect(ScreenState.NoSharingContentAvailable)
            return
        }

        val searchId = currentBookStore.searchId
        if (searchId.isNotEmpty()) {
            val targetUrl = resourceHelper.getString(R.string.ebook_snapshot_url, searchId)
            updateScreen(BookResultViewState.ShareCurrentPageSnapshot(targetUrl))
        }
    }

    // FIXME:: reimplement this event
    fun logISBNScanningSucceed() {
        eventTracker.logEvent(EventTracker.SCANNED_ISBN)
    }

    private fun isRequestingBookData(): Boolean {
        return networkJob?.isActive ?: false
    }

    private fun forceStopRequestingBookData() {
        networkJob?.cancel()
    }

    private fun networkRequestSuccess(bookStores: BookStores) {
        prepareBookSearchResult(bookStores)
    }

    private fun networkTimeout() {
        updateScreen(BookResultViewState.PrepareBookResultError)
        sendViewEffect(ScreenState.ConnectionTimeout)
    }

    private fun networkExceptionOccurred(message: String) {
        updateScreen(BookResultViewState.PrepareBookResultError)
        if (message == GENERIC_NETWORK_ISSUE) {
            sendViewEffect(ScreenState.NetworkError)
        } else {
            sendViewEffect(ScreenState.ShowToastMessage(-1, message))
        }
    }

    private fun sendViewEffect(screenState: ScreenState) {
        _screenViewState.value = ViewEffect(screenState)
    }

    /***
     * reduce
     */
    private fun updateScreen(bookResultViewState: BookResultViewState) {
        _bookResultViewState.value = bookResultViewState
    }
}
