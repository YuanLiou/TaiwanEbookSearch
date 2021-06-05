package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.*
import liou.rayyuan.ebooksearchtaiwan.model.*
import liou.rayyuan.ebooksearchtaiwan.model.dao.SearchRecordDao
import liou.rayyuan.ebooksearchtaiwan.model.domain.Result
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookResult
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.BookStores
import liou.rayyuan.ebooksearchtaiwan.model.domain.usecase.GetBooksWithStoresUseCase
import liou.rayyuan.ebooksearchtaiwan.model.entity.*
import liou.rayyuan.ebooksearchtaiwan.utils.DefaultStoreNames
import liou.rayyuan.ebooksearchtaiwan.utils.QuickChecker
import liou.rayyuan.ebooksearchtaiwan.utils.Utils
import java.net.SocketTimeoutException
import java.time.OffsetDateTime

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchViewModel(private val getBooksWithStoresUseCase: GetBooksWithStoresUseCase,
                          private val preferenceManager: UserPreferenceManager,
                          private val eventTracker: EventTracker,
                          private val quickChecker: QuickChecker,
                          private val searchRecordDao: SearchRecordDao) : ViewModel() {
    companion object {
        const val NO_MESSAGE = -1
        const val GENERIC_NETWORK_ISSUE = "generic-network-issue"
    }

    //region ViewStates
    private val _listViewState = MutableLiveData<ListViewState>()
    internal val listViewState: LiveData<ListViewState>
        get() = _listViewState

    private val _screenViewState = MutableLiveData<ScreenState>()
    internal val screenViewState: LiveData<ScreenState>
        get() = _screenViewState

    private val _searchRecordState = MutableLiveData<SearchRecordStates>()
    internal val searchRecordState: LiveData<SearchRecordStates>
        get() = _searchRecordState
    //endregion

    val searchRecordLiveData = run {
        val factory = searchRecordDao.getSearchRecordsPaged()
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(10)
                .setPageSize(10)
                .build()

        val pagedListBuilder = LivePagedListBuilder<Int, SearchRecord>(factory, config)
        pagedListBuilder.build()
    }

    private var networkJob: Job? = null
    private lateinit var fullBookStoreResultsAdapter: FullBookStoreResultAdapter
    private val maxListNumber: Int = 10
    private var eggCount: Int = 0
    private var bookStores: BookStores? = null
    private val defaultResultSort: List<DefaultStoreNames>
        get() {
            return preferenceManager.getBookStoreSort() ?: run {
                val defaultSort = Utils.getDefaultSort()
                preferenceManager.saveBookStoreSort(defaultSort)
                preferenceManager.getBookStoreSort()!!
            }
        }

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
            _listViewState.value = ListViewState.Prepare()
        } else {
            bookStores?.let {
                prepareBookSearchResult(it)
            }
        }
    }

    fun hintPressed() {
        eggCount++
        if (eggCount == 10) {
            _screenViewState.value = ScreenState.EasterEgg
            eventTracker.logEvent(EventTracker.SHOW_EASTER_EGG_01)
        }
        eventTracker.logEvent(EventTracker.CLICK_INFO_BUTTON)
    }

    fun focusOnEditText(isFocus: Boolean) {
        if (isFocus) {
            viewModelScope.launch {
                val recordCounts = withContext(Dispatchers.IO) {
                    searchRecordDao.getSearchRecordsCounts()
                }

                if (recordCounts > 0) {
                    _searchRecordState.value = SearchRecordStates.ShowList(recordCounts)
                } else {
                    _searchRecordState.value = SearchRecordStates.HideList
                }
            }
        } else {
            _searchRecordState.value = SearchRecordStates.HideList
        }
    }

    fun searchBook(keyword: String) {
        if (keyword.trim().isNotBlank()) {
            if (quickChecker.isInternetConnectionAvailable()) {
                if (isRequestingBookData()) {
                    forceStopRequestingBookData()
                }

                _listViewState.value = ListViewState.Prepare(true)
                networkJob = CoroutineScope(Dispatchers.IO).launch {
                    val response = getBooksWithStoresUseCase(defaultResultSort, keyword)
                    withContext(Dispatchers.Main) {
                        when (response) {
                            is Result.Success -> {
                                networkRequestSuccess(response.value)
                                resetCurrentResults()
                                saveKeywordToLocal(keyword)
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
                _screenViewState.value = ScreenState.NoInternetConnection
            }
        } else {
            _screenViewState.value = ScreenState.EmptyKeyword
        }

        _searchRecordState.value = SearchRecordStates.HideList
    }

    private suspend fun saveKeywordToLocal(keyword: String) = withContext(Dispatchers.IO) {
        searchRecordDao.getSearchRecordWithTitle(keyword)?.let {
            searchRecordDao.updateCounts(it.id, it.counts + 1, OffsetDateTime.now())
        } ?: run {
            val searchRecord = SearchRecord(keyword, 1, OffsetDateTime.now())
            searchRecordDao.insertRecords(listOf(searchRecord))
        }
    }

    fun deleteRecords(searchRecord: SearchRecord) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                searchRecordDao.deleteRecord(searchRecord)
            }
        }
    }

    private fun resetCurrentResults() {
        fullBookStoreResultsAdapter.clean()
    }

    fun setRecyclerViewAdapter(fullBookStoreResultsAdapter: FullBookStoreResultAdapter) {
        this.fullBookStoreResultsAdapter = fullBookStoreResultsAdapter
    }

    private fun prepareBookSearchResult(bookStores: BookStores) {
        this.bookStores = bookStores

        viewModelScope.launch {
            val adapterItems = generateAdapterItems(bookStores)
            _listViewState.value = ListViewState.Ready(lastScrollPosition, adapterItems)
            lastScrollPosition = 0
        }
    }

    private suspend fun generateAdapterItems(bookStores: BookStores) = withContext(Dispatchers.Default) {
        val adapterItems = mutableListOf<AdapterItem>()
        val groupedResults = bookStores.generateBookStoresResultMap(defaultResultSort)

        val bestItems = generateBestItems(groupedResults)
        adapterItems.add(
            BookHeader(
                DefaultStoreNames.BEST_RESULT.defaultResId,
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
                    storeName.defaultResId,
                    books.isEmpty(),
                    siteInfo = SiteInfo(
                        isOnline = bookResult.isOnline,
                        isResultOkay = bookResult.isOkay,
                        status = bookResult.status
                    ),
                )
            )

            books.let { resultList ->
                adapterItems.addAll(resultList)
            }
        }

        adapterItems.toList()
    }

    private fun generateBestItems(bookItems: Map<DefaultStoreNames, BookResult>): List<Book> {
        val bestItems = mutableListOf<Book>()
        bookItems.forEach{ (key, value) ->
            if (defaultResultSort.contains(key)) {
                val book = value.books.firstOrNull()
                book?.let { currentBook ->
                    currentBook.isFirstChoice = true
                    bestItems.add(currentBook)
                }
            }
        }
        bestItems.sortWith( compareBy { it.price })
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
        _screenViewState.value = ScreenState.ConnectionTimeout
    }

    private fun networkExceptionOccurred(message: String) {
        _listViewState.value = ListViewState.Error
        if (message == GENERIC_NETWORK_ISSUE) {
            _screenViewState.value = ScreenState.NetworkError
        } else {
            _screenViewState.value = ScreenState.ShowToastMessage(-1, message)
        }
    }
}