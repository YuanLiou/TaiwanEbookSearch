package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.*
import liou.rayyuan.ebooksearchtaiwan.model.*
import liou.rayyuan.ebooksearchtaiwan.model.dao.SearchRecordDao
import liou.rayyuan.ebooksearchtaiwan.model.entity.*
import liou.rayyuan.ebooksearchtaiwan.utils.DefaultStoreNames
import liou.rayyuan.ebooksearchtaiwan.utils.QuickChecker
import liou.rayyuan.ebooksearchtaiwan.utils.Utils
import liou.rayyuan.ebooksearchtaiwan.view.FullBookStoreResultAdapter
import okhttp3.ResponseBody
import org.threeten.bp.OffsetDateTime
import java.net.SocketTimeoutException

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchViewModel(private val apiManager: APIManager,
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

    internal val searchRecordLiveData = run {
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
    private val dataSetJob = Job()
    private val backgroundScope = CoroutineScope(Dispatchers.IO + dataSetJob)

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

    internal var lastScrollPosition: Int = 0
        internal set(value) {
            if (!isRequestingBookData()) {
                field = value
            } else {
                field = 0
            }
        }

    override fun onCleared() {
        if (dataSetJob.isActive && !dataSetJob.isCompleted) {
            dataSetJob.cancel()
        }
        forceStopRequestingBookData()
        eggCount = 0
        super.onCleared()
    }

    internal fun ready() {
        if (isRequestingBookData()) {
            _listViewState.value = ListViewState.Prepare()
        } else {
            bookStores?.let {
                prepareBookSearchResult(it)
            }
        }
    }

    internal fun hintPressed() {
        eggCount++
        if (eggCount == 10) {
            _screenViewState.value = ScreenState.EasterEgg
            eventTracker.logEvent(EventTracker.SHOW_EASTER_EGG_01)
        }
        eventTracker.logEvent(EventTracker.CLICK_INFO_BUTTON)
    }

    fun focusOnEditText(isFocus: Boolean) {
        if (isFocus) {
            backgroundScope.launch {
                val recordCounts = searchRecordDao.getSearchRecordsCounts()
                withContext(Dispatchers.Main) {
                    if (recordCounts > 0) {
                        _searchRecordState.value = SearchRecordStates.ShowList(recordCounts)
                    } else {
                        _searchRecordState.value = SearchRecordStates.HideList
                    }
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
                    val response = getBookList(keyword, true).await()
                    withContext(Dispatchers.Main) {
                        when (response) {
                            is HttpResult.Success -> {
                                networkRequestSuccess(response.data)
                                resetCurrentResults()
                                saveKeywordToLocal(keyword)
                            }
                            is HttpResult.Error -> {
                                networkErrorOccurred(null)
                            }
                            is HttpResult.ErrorInException -> {
                                if (response.exception is SocketTimeoutException) {
                                    networkTimeout()
                                } else {
                                    val message = response.exception.localizedMessage ?: GENERIC_NETWORK_ISSUE
                                    networkExceptionOccurred(message)
                                }
                            }
                            HttpResult.Empty -> {}
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

    private fun saveKeywordToLocal(keyword: String) {
        backgroundScope.launch {
            searchRecordDao.getSearchRecordWithTitle(keyword)?.let {
                searchRecordDao.updateCounts(it.id, it.counts + 1, OffsetDateTime.now())
            } ?: run {
                val searchRecord = SearchRecord(keyword, 1, OffsetDateTime.now())
                searchRecordDao.insertRecords(listOf(searchRecord))
            }
        }
    }

    internal fun deleteRecords(searchRecord: SearchRecord) {
        backgroundScope.launch {
            searchRecordDao.deleteRecord(searchRecord)
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
                _listViewState.value = ListViewState.Ready(lastScrollPosition, adapterItems)
                lastScrollPosition = 0
            }
        }
    }

    fun logISBNScanningSucceed() {
        eventTracker.logEvent(EventTracker.SCANNED_ISBN)
    }

    private fun getBookList(aboutSomething: String = "", force: Boolean): Deferred<HttpResult<BookStores>> {
        return CoroutineScope(Dispatchers.IO).async {
            try {
                if (force || (networkJob == null && aboutSomething.isNotEmpty())) {
                    val response = apiManager.getBooks(aboutSomething)
                    if (response.isSuccessful && response.body() != null) {
                        HttpResult.Success(response.body()!!)
                    } else {
                        HttpResult.Error(null, true)
                    }
                } else {
                    HttpResult.Empty
                }
            } catch (error: Throwable) {
                HttpResult.ErrorInException(error)
            }
        }
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

    private fun networkErrorOccurred(errorBody: ResponseBody?) {
        _listViewState.value = ListViewState.Error
        _screenViewState.value = ScreenState.NetworkError
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