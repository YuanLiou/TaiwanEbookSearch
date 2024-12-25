package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.rayliu.commonmain.BookStoresSorter
import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.model.BookResult
import com.rayliu.commonmain.domain.model.BookStoreDetails
import com.rayliu.commonmain.domain.model.BookStores
import com.rayliu.commonmain.domain.model.SearchRecord
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import com.rayliu.commonmain.domain.usecase.DeleteSearchRecordUseCase
import com.rayliu.commonmain.domain.usecase.GetBookStoresDetailUseCase
import com.rayliu.commonmain.domain.usecase.GetBooksWithStoresUseCase
import com.rayliu.commonmain.domain.usecase.GetDefaultBookSortUseCase
import com.rayliu.commonmain.domain.usecase.GetSearchRecordsCountsUseCase
import com.rayliu.commonmain.domain.usecase.GetSearchRecordsUseCase
import com.rayliu.commonmain.domain.usecase.GetSearchSnapshotUseCase
import java.net.SocketTimeoutException
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.arch.IModel
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.FocusAction
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.VirtualKeyboardAction
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.BookSearchResultItem
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.BookHeader
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.SiteInfo
import liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate.BookResultViewState
import liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate.ScreenState
import liou.rayyuan.ebooksearchtaiwan.interactor.UserRankingWindowFacade
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.BookUiModel
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.asUiModel
import liou.rayyuan.ebooksearchtaiwan.navigation.BookResultDestinations
import liou.rayyuan.ebooksearchtaiwan.utils.ClipboardHelper
import liou.rayyuan.ebooksearchtaiwan.utils.QuickChecker
import liou.rayyuan.ebooksearchtaiwan.utils.ResourceHelper
import liou.rayyuan.ebooksearchtaiwan.view.getStringResource

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchViewModel(
    private val getBooksWithStoresUseCase: GetBooksWithStoresUseCase,
    private val getSearchRecordsUseCase: GetSearchRecordsUseCase,
    private val getSearchRecordsCountsUseCase: GetSearchRecordsCountsUseCase,
    private val getDefaultBookSortUseCase: GetDefaultBookSortUseCase,
    private val getSearchSnapshotUseCase: GetSearchSnapshotUseCase,
    private val getBookStoresDetailUseCase: GetBookStoresDetailUseCase,
    private val quickChecker: QuickChecker,
    private val deleteSearchRecordUseCase: DeleteSearchRecordUseCase,
    private val resourceHelper: ResourceHelper,
    private val rankingWindowFacade: UserRankingWindowFacade,
    private val clipboardHelper: ClipboardHelper,
    private val userPreferenceManager: UserPreferenceManager
) : ViewModel(),
    IModel<BookResultViewState, BookSearchUserIntent> {
    override val userIntents: MutableSharedFlow<BookSearchUserIntent> = MutableSharedFlow()
    private val _bookResultViewState = MutableLiveData<BookResultViewState>()
    override val viewState: LiveData<BookResultViewState>
        get() = _bookResultViewState

    private val _screenViewState = MutableSharedFlow<ScreenState>()
    val screenViewState: SharedFlow<ScreenState>
        get() = _screenViewState.asSharedFlow()

    private val _bookStoreDetails = MutableStateFlow<ImmutableList<BookStoreDetails>>(persistentListOf())
    val bookStoreDetails
        get() = _bookStoreDetails.asStateFlow()

    private val _bookSearchResult = MutableStateFlow<ImmutableList<BookSearchResultItem>>(persistentListOf())
    val bookSearchResult
        get() = _bookSearchResult.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<BookResultDestinations>()
    val navigationEvents
        get() = _navigationEvents.asSharedFlow()

    private val _searchKeywords = MutableStateFlow(TextFieldValue(""))
    val searchKeywords
        get() = _searchKeywords.asStateFlow()

    private val _isTextInputFocused = MutableStateFlow(false)
    val isTextInputFocused
        get() = _isTextInputFocused.asStateFlow()

    private val _focusTextInput = MutableStateFlow(FocusAction.NEUTRAL_STATE)
    val focusTextInput
        get() = _focusTextInput.asStateFlow()

    private val _showVirtualKeyboard = MutableStateFlow(VirtualKeyboardAction.NEUTRAL_STATE)
    val showVirtualKeyboard
        get() = _showVirtualKeyboard.asStateFlow()

    private val _enableCameraButtonClick = MutableStateFlow(true)
    val enableCameraButtonClick
        get() = _enableCameraButtonClick.asStateFlow()

    private val _enableSearchButtonClick = MutableStateFlow(true)
    val enableSearchButtonClick
        get() = _enableSearchButtonClick.asStateFlow()

    val searchRecordLiveData by lazy {
        getSearchRecordsUseCase().cachedIn(viewModelScope)
    }

    var showCopyUrlOption by mutableStateOf(false)
    var showShareSnapshotOption by mutableStateOf(false)

    private var networkJob: Job? = null
    private val maxListNumber: Int = 10
    private var bookStores: BookStores? = null
    private var previousKeyword: String? = null
    val hasPreviousSearch: Boolean
        get() = bookStores != null

    var lastScrollPosition: Int = 0
        private set(value) {
            field =
                if (!isRequestingBookData()) {
                    value
                } else {
                    0
                }
        }
    var lastScrollOffset: Int = 0
        private set(value) {
            field =
                if (!isRequestingBookData()) {
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
            userIntents.collect { userIntent ->
                when (userIntent) {
                    is BookSearchUserIntent.DeleteSearchRecord -> {
                        deleteRecords(userIntent.searchRecord)
                    }

                    is BookSearchUserIntent.FocusOnTextEditing -> {
                        focusOnEditText(userIntent.isFocus)
                    }

                    BookSearchUserIntent.OnViewReadyToServe -> {
                        ready()
                    }

                    is BookSearchUserIntent.SearchBook -> {
                        val keyword = userIntent.keywords
                        if (keyword != null) {
                            searchBook(userIntent.keywords)
                        } else {
                            searchBook(searchKeywords.value.text)
                        }
                    }

                    is BookSearchUserIntent.ShowSearchSnapshot -> {
                        requestSearchSnapshot(userIntent.searchId)
                    }

                    BookSearchUserIntent.ShareSnapshot -> {
                        shareCurrentSnapshot()
                    }

                    is BookSearchUserIntent.AskUserRankApp -> {
                        val hasUserSeenRankWindow =
                            runCatching {
                                checkUserHasSeenRankWindow()
                            }.getOrDefault(false)

                        if (!hasUserSeenRankWindow) {
                            sendViewEffect(ScreenState.ShowUserRankingDialog(userIntent.reviewInfo))
                        }
                    }

                    BookSearchUserIntent.RankAppWindowHasShown -> {
                        rankingWindowFacade.saveUserHasSeenRankWindow()
                    }

                    BookSearchUserIntent.CopySnapshotUrlToClipboard -> {
                        copySnapshotToClipboard()
                    }

                    BookSearchUserIntent.CheckServiceStatus -> {
                        checkServiceStatus()
                    }

                    is BookSearchUserIntent.UpdateKeyword -> {
                        _searchKeywords.value = userIntent.keywords
                    }

                    is BookSearchUserIntent.UpdateTextInputFocusState -> {
                        _isTextInputFocused.value = userIntent.isFocused
                    }

                    BookSearchUserIntent.ResetFocusAction -> {
                        _focusTextInput.value = FocusAction.NEUTRAL_STATE
                    }

                    is BookSearchUserIntent.ForceFocusOrUnfocusKeywordTextInput -> {
                        if (userIntent.focus) {
                            _focusTextInput.value = FocusAction.FOCUS
                        } else {
                            _focusTextInput.value = FocusAction.UNFOCUS
                        }
                    }

                    BookSearchUserIntent.ResetVirtualKeyboardAction -> {
                        _showVirtualKeyboard.value = VirtualKeyboardAction.NEUTRAL_STATE
                    }

                    is BookSearchUserIntent.ForceShowOrHideVirtualKeyboard -> {
                        if (userIntent.show) {
                            _showVirtualKeyboard.value = VirtualKeyboardAction.SHOW
                        } else {
                            _showVirtualKeyboard.value = VirtualKeyboardAction.HIDE
                        }
                    }

                    is BookSearchUserIntent.EnableCameraButtonClick -> {
                        _enableCameraButtonClick.value = userIntent.enable
                    }

                    is BookSearchUserIntent.EnableSearchButtonClick -> {
                        _enableSearchButtonClick.value = userIntent.enable
                    }

                    is BookSearchUserIntent.ShowCopyUrlOption -> {
                        showCopyUrlOption = userIntent.show
                    }

                    is BookSearchUserIntent.ShowShareSnapshotOption -> {
                        showShareSnapshotOption = userIntent.show
                    }
                }
            }
        }
    }

    override fun onCleared() {
        forceStopRequestingBookData()
        super.onCleared()
    }

    fun savePreviousScrollPosition(
        position: Int,
        offset: Int
    ) {
        lastScrollPosition = position
        lastScrollOffset = offset
    }

    private fun ready() {
        if (isRequestingBookData()) {
            updateScreen(BookResultViewState.PrepareBookResult)
            updateBookSearchScreen(BookResultDestinations.LoadingScreen)
        } else {
            bookStores?.let {
                prepareBookSearchResult(it)
            }
        }
    }

    private fun focusOnEditText(isFocus: Boolean) {
        if (isFocus) {
            viewModelScope.launch {
                getSearchRecordsCountsUseCase().fold(
                    onSuccess = { recordCounts ->
                        if (recordCounts > 0) {
                            updateScreen(BookResultViewState.ShowSearchRecordList(recordCounts))
                        } else {
                            updateScreen(BookResultViewState.HideSearchRecordList)
                        }
                    },
                    onFailure = {
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

    private inner class SearchBookAction(
        private val keyword: String
    ) : suspend () -> Result<BookStores> {
        override suspend fun invoke(): Result<BookStores> {
            this@BookSearchViewModel.previousKeyword = keyword
            val defaultSort = getDefaultBookSortUseCase().first()
            return getBooksWithStoresUseCase(defaultSort, keyword)
        }
    }

    private fun fetchBookResult(action: suspend () -> Result<BookStores>) {
        if (!quickChecker.isInternetConnectionAvailable()) {
            sendViewEffect(ScreenState.NoInternetConnection)
            return
        }

        if (isRequestingBookData()) {
            forceStopRequestingBookData()
        }

        updateScreen(BookResultViewState.PrepareBookResult)
        updateBookSearchScreen(BookResultDestinations.LoadingScreen)
        bookStores = null // clean up
        networkJob =
            viewModelScope.launch(Dispatchers.IO) {
                val response = action.invoke()
                withContext(Dispatchers.Main) {
                    response.fold(
                        onSuccess = {
                            networkRequestSuccess(it)
                        },
                        onFailure = {
                            // ServerResponseException == internal server error
                            // ClientRequestException == response.status.value to get response code
                            // RedirectResponseException
                            this@BookSearchViewModel.previousKeyword = null
                            if (it is SocketTimeoutException) {
                                networkTimeout()
                            } else {
                                val message = it.localizedMessage ?: GENERIC_NETWORK_ISSUE
                                networkExceptionOccurred(message)
                            }
                        }
                    )
                }
            }
        updateScreen(BookResultViewState.HideSearchRecordList)
    }

    private inner class ShowSearchSnapshotAction(
        private val searchId: String
    ) : suspend () -> Result<BookStores> {
        override suspend fun invoke(): Result<BookStores> {
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
            val bookSearchResultItems = generateBookSearchResultItems(bookStores)
            _bookSearchResult.value = bookSearchResultItems.toImmutableList()
            updateScreen(BookResultViewState.ShowBooks(bookStores.searchKeyword))
            updateBookSearchScreen(BookResultDestinations.SearchResult)
        }
    }

    private suspend fun generateBookSearchResultItems(bookStores: BookStores) =
        withContext(Dispatchers.Default) {
            val bookSearchResultItems = mutableListOf<BookSearchResultItem>()
            val defaultSort = getDefaultBookSortUseCase().first()
            val groupedResults = BookStoresSorter.generateResultMap(bookStores, defaultSort)

            val bestItems = generateBestItems(defaultSort, groupedResults)
            bookSearchResultItems.add(
                BookHeader(
                    DefaultStoreNames.BEST_RESULT.getStringResource(),
                    bestItems.isEmpty(),
                    siteInfo = null
                )
            )
            bookSearchResultItems.addAll(bestItems)

            for (storeName in defaultSort) {
                val bookResult = groupedResults[storeName] ?: continue
                val books =
                    bookResult.books.run {
                        drop(1)
                    }.run {
                        take(maxListNumber)
                    }.run {
                        if (userPreferenceManager.isSearchResultSortByPrice()) {
                            sortedBy { it.price }
                        } else {
                            sortedByDescending { it.titleKeywordSimilarity }
                        }
                    }

                bookSearchResultItems.add(
                    BookHeader(
                        storeName.getStringResource(),
                        books.isEmpty(),
                        siteInfo =
                            SiteInfo(
                                isOnline = bookResult.isOnline,
                                isResultOkay = bookResult.isOkay,
                                status = bookResult.status
                            )
                    )
                )

                bookSearchResultItems.addAll(
                    books.map { it.asUiModel() }
                )
            }

            bookSearchResultItems.toList()
        }

    private fun generateBestItems(
        defaultSort: List<DefaultStoreNames>,
        bookItems: Map<DefaultStoreNames, BookResult>
    ): List<BookUiModel> {
        val bestItems = mutableListOf<BookUiModel>()
        bookItems.forEach { (key, value) ->
            if (defaultSort.contains(key)) {
                val book = value.books.firstOrNull()
                book?.let { currentBook ->
                    currentBook.isFirstChoice = true
                    bestItems.add(currentBook.asUiModel())
                }
            }
        }
        bestItems.sortWith(compareBy { it.book.price })
        return bestItems
    }

    private fun shareCurrentSnapshot() {
        generateSnapshotUrl { targetUrl ->
            updateScreen(BookResultViewState.ShareCurrentPageSnapshot(targetUrl))
        }
    }

    private fun copySnapshotToClipboard() {
        generateSnapshotUrl { targetUrl ->
            clipboardHelper.addToClipboard(targetUrl)
            sendViewEffect(ScreenState.ShowToastMessage(R.string.add_to_clipboard_successful))
        }
    }

    private fun generateSnapshotUrl(action: (String) -> Unit) {
        val currentBookStore =
            bookStores ?: run {
                sendViewEffect(ScreenState.NoSharingContentAvailable)
                return
            }

        val searchId = currentBookStore.searchId
        if (searchId.isNotEmpty()) {
            val targetUrl = resourceHelper.getString(R.string.ebook_snapshot_url, searchId)
            action(targetUrl)
        }
    }

    private fun isRequestingBookData(): Boolean = networkJob?.isActive ?: false

    private fun forceStopRequestingBookData() {
        networkJob?.cancel()
    }

    private fun networkRequestSuccess(bookStores: BookStores) {
        prepareBookSearchResult(bookStores)
    }

    private fun networkTimeout() {
        updateScreen(BookResultViewState.PrepareBookResultError)
        updateBookSearchScreen(BookResultDestinations.ServiceStatus)
        sendViewEffect(ScreenState.ConnectionTimeout)
    }

    private fun networkExceptionOccurred(message: String) {
        updateScreen(BookResultViewState.PrepareBookResultError)
        updateBookSearchScreen(BookResultDestinations.ServiceStatus)
        if (message == GENERIC_NETWORK_ISSUE) {
            sendViewEffect(ScreenState.NetworkError)
        } else {
            sendViewEffect(ScreenState.ShowToastMessage(-1, message))
        }
    }

    private fun checkServiceStatus() {
        if (viewState.value is BookResultViewState.PrepareBookResult || viewState.value is BookResultViewState.ShowBooks) {
            return
        }

        Log.i(TAG, "loading service status")

        viewModelScope.launch {
            getBookStoresDetailUseCase().fold(
                onSuccess = { bookStoreDetails ->
                    _bookStoreDetails.value = bookStoreDetails
                },
                onFailure = { error ->
                    Log.e(TAG, Log.getStackTraceString(error))
                }
            )
        }
    }

    private fun sendViewEffect(screenState: ScreenState) {
        viewModelScope.launch {
            _screenViewState.emit(screenState)
        }
    }

    suspend fun checkUserHasSeenRankWindow(): Boolean = rankingWindowFacade.isUserSeenRankWindow().firstOrNull() ?: false

    private fun updateScreen(bookResultViewState: BookResultViewState) {
        _bookResultViewState.value = bookResultViewState
    }

    private fun updateBookSearchScreen(bookSearchDestination: BookResultDestinations) {
        viewModelScope.launch {
            _navigationEvents.emit(bookSearchDestination)
        }
    }

    companion object {
        const val NO_MESSAGE = -1
        const val GENERIC_NETWORK_ISSUE = "generic-network-issue"
        private const val TAG = "BookSearchViewModel"
    }
}
