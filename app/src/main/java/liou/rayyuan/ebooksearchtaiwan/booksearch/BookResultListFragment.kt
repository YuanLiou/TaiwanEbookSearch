package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withResumed
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rayliu.commonmain.domain.model.Book
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.BaseFragment
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.arch.IView
import liou.rayyuan.ebooksearchtaiwan.booksearch.review.PlayStoreReviewHelper
import liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate.BookResultViewState
import liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate.ScreenState
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.utils.FragmentArgumentsDelegate
import liou.rayyuan.ebooksearchtaiwan.utils.showToastOn
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookResultListFragment :
    BaseFragment(),
    IView<BookResultViewState> {
    private val bookSearchViewModel: BookSearchViewModel by viewModel()
    private val playStoreReviewHelper: PlayStoreReviewHelper by inject()
    private var defaultSearchKeyword: String by FragmentArgumentsDelegate()
    private var defaultSnapshotSearchId: String by FragmentArgumentsDelegate()

    private var hasUserSeenRankWindow = false
    private var openResultCounts = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
        setContent {
            EBookTheme(
                darkTheme = isDarkTheme()
            ) {
                BookResultListScreen(
                    viewModel = bookSearchViewModel,
                    modifier = Modifier.fillMaxSize(),
                    onBookSearchItemClick = ::openBook,
                    showAppBarCameraButton = isCameraAvailable(),
                    onAppBarCameraButtonPress = {
                        openCameraPreview()
                    },
                    onMenuSettingClick = {
                        if (isAdded) {
                            (requireActivity() as? BookSearchActivity)?.openPreferenceActivity()
                        }
                    }
                )
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        initAdMods()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        // Render View Effect
        viewLifecycleOwner.lifecycleScope.launch {
            bookSearchViewModel.screenViewState.collect {
                updateScreen(it)
            }
        }

        // Render Book Result State
        bookSearchViewModel.viewState.observe(
            viewLifecycleOwner
        ) { state -> render(state) }

        sendUserIntent(BookSearchUserIntent.OnViewReadyToServe)
        handleInitialDeepLink()

        viewLifecycleOwner.lifecycleScope.launch {
            hasUserSeenRankWindow = bookSearchViewModel.checkUserHasSeenRankWindow()
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                sendUserIntent(BookSearchUserIntent.CheckServiceStatus)
            }
        }
    }

    private fun handleInitialDeepLink() {
        lifecycleScope.launch {
            withResumed {
                if (defaultSearchKeyword.isNotBlank()) {
                    searchWithText(defaultSearchKeyword)
                    defaultSearchKeyword = ""
                } else if (defaultSnapshotSearchId.isNotBlank()) {
                    showSearchSnapshot(defaultSnapshotSearchId)
                    defaultSnapshotSearchId = ""
                }
            }
        }
    }

    private fun initAdMods() {
        MobileAds.initialize(requireContext())
        val configurationBuilder = RequestConfiguration.Builder()
        if (BuildConfig.DEBUG) {
            configurationBuilder.setTestDeviceIds(listOf(BuildConfig.ADMOB_TEST_DEVICE_ID))
        }
        MobileAds.setRequestConfiguration(configurationBuilder.build())
    }

    override fun render(viewState: BookResultViewState) {
        renderMainResultView(viewState)
    }

    private fun renderMainResultView(bookResultViewState: BookResultViewState) {
        when (bookResultViewState) {
            is BookResultViewState.PrepareBookResult -> {
                sendUserIntent(BookSearchUserIntent.EnableCameraButtonClick(false))
                sendUserIntent(BookSearchUserIntent.EnableSearchButtonClick(false))
                sendUserIntent(BookSearchUserIntent.ShowCopyUrlOption(false))
                sendUserIntent(BookSearchUserIntent.ShowShareSnapshotOption(false))
            }

            is BookResultViewState.ShowBooks -> {
                sendUserIntent(BookSearchUserIntent.EnableCameraButtonClick(true))
                sendUserIntent(BookSearchUserIntent.EnableSearchButtonClick(true))

                if (bookResultViewState.keyword.isNotEmpty()) {
                    changeSearchBoxKeyword(bookResultViewState.keyword)
                }

                sendUserIntent(BookSearchUserIntent.ShowCopyUrlOption(true))
                sendUserIntent(BookSearchUserIntent.ShowShareSnapshotOption(true))
            }

            BookResultViewState.PrepareBookResultError -> {
                sendUserIntent(BookSearchUserIntent.EnableCameraButtonClick(true))
                sendUserIntent(BookSearchUserIntent.EnableSearchButtonClick(true))
                sendUserIntent(BookSearchUserIntent.ShowCopyUrlOption(false))
                sendUserIntent(BookSearchUserIntent.ShowShareSnapshotOption(false))
            }

            is BookResultViewState.ShareCurrentPageSnapshot -> {
                val intent = Intent(Intent.ACTION_SEND)
                with(intent) {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "From " + getString(R.string.app_name))
                    putExtra(Intent.EXTRA_TEXT, bookResultViewState.url)
                }
                startActivity(
                    Intent.createChooser(
                        intent,
                        getString(R.string.menu_share_menu_appear)
                    )
                )
            }
        }
    }

    private fun updateScreen(screenState: ScreenState) {
        when (screenState) {
            ScreenState.ConnectionTimeout -> {
                showInternetConnectionTimeout()
            }

            ScreenState.NetworkError -> {
                showNetworkErrorMessage()
            }

            ScreenState.EmptyKeyword -> {
                showKeywordIsEmpty()
            }

            ScreenState.NoInternetConnection -> {
                showInternetRequestDialog()
            }

            is ScreenState.ShowToastMessage -> {
                if (screenState.stringResId != BookSearchViewModel.NO_MESSAGE) {
                    showToast(getString(screenState.stringResId))
                    return
                }
                if (screenState.message != null) {
                    showToast(screenState.message)
                }
            }

            ScreenState.NoSharingContentAvailable -> {
                showToast(getString(R.string.no_shareable_content))
            }

            is ScreenState.ShowUserRankingDialog -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    playStoreReviewHelper.showReviewDialog(requireActivity(), screenState.reviewInfo)
                    // Reset counts and flags
                    hasUserSeenRankWindow = true
                    openResultCounts = 0
                }
            }
        }
    }

    private fun openBook(book: Book) {
        if (isAdded) {
            (requireActivity() as? BookSearchActivity)?.openBookLink(book)
            if (!hasUserSeenRankWindow) {
                openResultCounts++
            }
        }
    }

    fun checkShouldAskUserRankApp() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (openResultCounts < POPUP_REVIEW_WINDOW_THRESHOLD) {
                return@launch
            }

            val hasUserSeenRankWindow =
                bookSearchViewModel.checkUserHasSeenRankWindow().also {
                    this@BookResultListFragment.hasUserSeenRankWindow = it
                }

            if (hasUserSeenRankWindow) {
                return@launch
            }

            if (BuildConfig.DEBUG) {
                Toast.makeText(requireContext(), "Rank Window Should Popup", Toast.LENGTH_SHORT).show()
            }

            val reviewInfo =
                runCatching {
                    playStoreReviewHelper.prepareReviewInfo()
                }.getOrNull()

            if (reviewInfo != null) {
                sendUserIntent(BookSearchUserIntent.AskUserRankApp(reviewInfo))
            }
        }
    }

    private fun showInternetRequestDialog() {
        if (isAdded) {
            val dialogBuilder = MaterialAlertDialogBuilder(requireActivity())
            dialogBuilder.setTitle(R.string.network_alert_dialog_title)
            dialogBuilder.setMessage(R.string.network_alert_message)
            dialogBuilder.setPositiveButton(R.string.dialog_ok) { _: DialogInterface, _: Int -> }
            dialogBuilder.create().show()
        }
    }

    private fun showInternetConnectionTimeout() {
        if (isAdded) {
            getString(R.string.state_timeout).showToastOn(requireContext())
        }
    }

    private fun showKeywordIsEmpty() {
        if (isAdded) {
            Toast.makeText(requireContext(), R.string.search_keyword_empty, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun hideVirtualKeyboard() {
        bookSearchViewModel.forceShowOrHideVirtualKeyboard(false)
    }

    private fun showNetworkErrorMessage() {
        if (isAdded) {
            getString(R.string.network_error_message).showToastOn(requireContext())
        }
    }

    private fun showToast(message: String) {
        if (isAdded) {
            message.showToastOn(requireContext())
        }
    }

    private fun openCameraPreview() {
        if (isAdded) {
            (requireActivity() as? BookSearchActivity)?.openCameraPreviewActivity()
        }
    }

    private fun isCameraAvailable(): Boolean {
        if (isAdded) {
            return requireActivity().packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
                ?: false
        }
        return false
    }

    fun searchWithText(text: String) {
        changeSearchBoxKeyword(text)
        hideVirtualKeyboard()
        bookSearchViewModel.searchBook(text)
    }

    private fun changeSearchBoxKeyword(keyword: String) {
        bookSearchViewModel.updateKeyword(TextFieldValue(keyword, selection = TextRange(keyword.length)))
        bookSearchViewModel.forceFocusOrUnfocusKeywordTextInput(false)
    }

    fun showSearchSnapshot(searchId: String) {
        hideVirtualKeyboard()
        sendUserIntent(BookSearchUserIntent.ShowSearchSnapshot(searchId))
    }

    fun backPressed(): Boolean {
        if (bookSearchViewModel.isTextInputFocused.value) {
            bookSearchViewModel.forceFocusOrUnfocusKeywordTextInput(false)
            return true
        }

        return false
    }

    fun toggleSearchRecordView(show: Boolean) {
        bookSearchViewModel.showSearchRecords(show)
    }

    private fun sendUserIntent(userIntent: BookSearchUserIntent) {
        lifecycleScope.launch {
            bookSearchViewModel.userIntents.emit(userIntent)
        }
    }

    companion object {
        private const val POPUP_REVIEW_WINDOW_THRESHOLD = 5

        fun newInstance(
            defaultKeyword: String?,
            defaultSnapshotSearchId: String?
        ) = BookResultListFragment().apply {
            this.defaultSearchKeyword = defaultKeyword.orEmpty()
            this.defaultSnapshotSearchId = defaultSnapshotSearchId.orEmpty()
        }

        const val TAG = "book-result-list-fragment"
    }
}
