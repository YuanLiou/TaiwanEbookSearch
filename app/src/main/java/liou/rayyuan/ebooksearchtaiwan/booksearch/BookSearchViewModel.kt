package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.AdapterItem
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.BookHeader
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.SiteInfo
import liou.rayyuan.ebooksearchtaiwan.model.*
import com.rayliu.commonmain.domain.Result
import com.rayliu.commonmain.domain.model.BookResult
import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.generateBookStoresResultMap
import liou.rayyuan.ebooksearchtaiwan.utils.QuickChecker
import com.rayliu.commonmain.domain.model.SearchRecord
import com.rayliu.commonmain.domain.usecase.*
import liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate.BookResultViewState
import liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate.ScreenState
import liou.rayyuan.ebooksearchtaiwan.view.ViewEvent
import liou.rayyuan.ebooksearchtaiwan.view.getStringResource
import liou.rayyuan.ebooksearchtaiwan.viewmodel.BookViewModel
import java.net.SocketTimeoutException

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchViewModel(
    private val getBooksWithStoresUseCase: GetBooksWithStoresUseCase,
    private val getSearchRecordsUseCase: GetSearchRecordsUseCase,
    private val getSearchRecordsCountsUseCase: GetSearchRecordsCountsUseCase,
    private val getDefaultBookSortUseCase: GetDefaultBookSortUseCase,
    private val eventTracker: EventTracker,
    private val quickChecker: QuickChecker,
    private val deleteSearchRecordUseCase: DeleteSearchRecordUseCase
) : ViewModel() {
    companion object {
        const val NO_MESSAGE = -1
        const val GENERIC_NETWORK_ISSUE = "generic-network-issue"
    }

    //region ViewStates
    private val _bookResultViewState = MutableLiveData<BookResultViewState>()
    val bookResultViewState: LiveData<BookResultViewState>
        get() = _bookResultViewState

    private val _screenViewState = MutableLiveData<ViewEvent<ScreenState>>()
    val screenViewState: LiveData<ViewEvent<ScreenState>>
        get() = _screenViewState
    //endregion

    val searchRecordLiveData by lazy {
        getSearchRecordsUseCase()
    }

    private var networkJob: Job? = null
    private val maxListNumber: Int = 10
    private var eggCount: Int = 0
    private var bookStores: BookStores? = null
    private val defaultResultSort: List<DefaultStoreNames>
        get() = getDefaultBookSortUseCase()

    var lastScrollPosition: Int = 0
        set(value) {
            if (!isRequestingBookData()) {
                field = value
            } else {
                field = 0
            }
        }

    override fun onCleared() {
        forceStopRequestingBookData()
        eggCount = 0
        super.onCleared()
    }

    fun ready() {
        if (isRequestingBookData()) {
            _bookResultViewState.value = BookResultViewState.PrepareBookResult()
        } else {
            bookStores?.let {
                prepareBookSearchResult(it)
            }
        }
    }

    fun hintPressed() {
        eggCount++
        if (eggCount == 10) {
            sendViewEvent(ScreenState.EasterEgg)
            eventTracker.logEvent(EventTracker.SHOW_EASTER_EGG_01)
        }
        eventTracker.logEvent(EventTracker.CLICK_INFO_BUTTON)
    }

    fun focusOnEditText(isFocus: Boolean) {
        if (isFocus) {
            viewModelScope.launch {
                getSearchRecordsCountsUseCase().fold(
                    success = { recordCounts ->
                        if (recordCounts > 0) {
                            _bookResultViewState.value = BookResultViewState.ShowSearchRecordList(recordCounts)
                        } else {
                            _bookResultViewState.value = BookResultViewState.HideSearchRecordList
                        }
                    },
                    failure = {
                        _bookResultViewState.value = BookResultViewState.HideSearchRecordList
                    }
                )
            }
        } else {
            _bookResultViewState.value = BookResultViewState.HideSearchRecordList
        }
    }

    fun searchBook(keyword: String) {
        if (keyword.trim().isNotBlank()) {
            if (quickChecker.isInternetConnectionAvailable()) {
                if (isRequestingBookData()) {
                    forceStopRequestingBookData()
                }

                _bookResultViewState.value = BookResultViewState.PrepareBookResult(true)
                networkJob = CoroutineScope(Dispatchers.IO).launch {
                    val response = getBooksWithStoresUseCase(defaultResultSort, keyword)
                    withContext(Dispatchers.Main) {
                        when (response) {
                            is Result.Success -> {
                                networkRequestSuccess(response.value)
                            }
                            is Result.Failed -> {
                                /*
                                ServerResponseException == internal server error
                                ClientRequestException == response.status.value to get response code
                                RedirectResponseException
                                 */
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
            } else {
                sendViewEvent(ScreenState.NoInternetConnection)
            }
        } else {
            sendViewEvent(ScreenState.EmptyKeyword)
        }
        _bookResultViewState.value = BookResultViewState.HideSearchRecordList
    }

    fun deleteRecords(searchRecord: SearchRecord) {
        viewModelScope.launch {
            deleteSearchRecordUseCase(searchRecord)
        }
    }

    private fun prepareBookSearchResult(bookStores: BookStores) {
        this.bookStores = bookStores

        viewModelScope.launch {
            val adapterItems = generateAdapterItems(bookStores)
            _bookResultViewState.value = BookResultViewState.ShowBooks(lastScrollPosition, adapterItems)
            lastScrollPosition = 0
        }
    }

    private suspend fun generateAdapterItems(bookStores: BookStores) = withContext(Dispatchers.Default) {
        val adapterItems = mutableListOf<AdapterItem>()
        val groupedResults = bookStores.generateBookStoresResultMap(defaultResultSort)

        val bestItems = generateBestItems(groupedResults)
        adapterItems.add(
            BookHeader(
                DefaultStoreNames.BEST_RESULT.getStringResource(),
                bestItems.isEmpty(),
                siteInfo = null
            )
        )
        adapterItems.addAll(bestItems)

        for (storeName in defaultResultSort) {
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

            books.let { resultList ->
                adapterItems.addAll(
                    resultList.map { BookViewModel(it)}
                )
            }
        }

        adapterItems.toList()
    }

    private fun generateBestItems(bookItems: Map<DefaultStoreNames, BookResult>): List<BookViewModel> {
        val bestItems = mutableListOf<BookViewModel>()
        bookItems.forEach{ (key, value) ->
            if (defaultResultSort.contains(key)) {
                val book = value.books.firstOrNull()
                book?.let { currentBook ->
                    currentBook.isFirstChoice = true
                    bestItems.add(BookViewModel(currentBook))
                }
            }
        }
        bestItems.sortWith( compareBy { it.book.price })
        return bestItems
    }

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
        sendViewEvent(ScreenState.ConnectionTimeout)
    }

    private fun networkExceptionOccurred(message: String) {
        _bookResultViewState.value = BookResultViewState.PrepareBookResultError
        if (message == GENERIC_NETWORK_ISSUE) {
            sendViewEvent(ScreenState.NetworkError)
        } else {
            sendViewEvent(ScreenState.ShowToastMessage(-1, message))
        }
    }

    private fun sendViewEvent(screenState: ScreenState) {
        _screenViewState.value = ViewEvent(screenState)
    }
}