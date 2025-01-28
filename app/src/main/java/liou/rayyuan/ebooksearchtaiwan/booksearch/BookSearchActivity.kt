package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withResumed
import androidx.lifecycle.withStarted
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.arch.IView
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.asUiModel
import liou.rayyuan.ebooksearchtaiwan.booksearch.review.PlayStoreReviewHelper
import liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate.BookResultViewState
import liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate.ScreenState
import liou.rayyuan.ebooksearchtaiwan.camerapreview.CameraPreviewActivity
import liou.rayyuan.ebooksearchtaiwan.model.DeeplinkHelper
import liou.rayyuan.ebooksearchtaiwan.preferencesetting.PreferenceSettingsActivity
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.utils.CustomTabSessionManager
import liou.rayyuan.ebooksearchtaiwan.utils.showToastOn
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by louis383 on 2017/12/2.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
class BookSearchActivity :
    BaseActivity(),
    IView<BookResultViewState> {
    private val customTabSessionManager: CustomTabSessionManager by inject()
    private val deeplinkHelper: DeeplinkHelper by inject()
    private val bookSearchViewModel: BookSearchViewModel by viewModel()
    private val playStoreReviewHelper: PlayStoreReviewHelper by inject()

    private var defaultSearchKeyword: String = ""
    private var defaultSnapshotSearchId: String = ""

    private var hasUserSeenRankWindow = false
    private var openResultCounts = 0

    private lateinit var changeThemeLauncher: ActivityResultLauncher<Intent>

    private val barcodeScannerResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val bundle = activityResult.data?.extras
                val resultText = bundle?.getString(KEY_BARCODE_RESULT, "").orEmpty()
                if (resultText.isNotEmpty()) {
                    searchWithText(resultText)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        initAdMods()
        setupLauncherCallbacks()

        if (savedInstanceState == null) {
            defaultSearchKeyword = deeplinkHelper.getSearchKeyword(intent).orEmpty()
            defaultSnapshotSearchId = deeplinkHelper.getSearchId(intent).orEmpty()
        }

        lifecycleScope.launch {
            if (userPreferenceManager.isPreferCustomTab()) {
                customTabSessionManager.bindCustomTabService(this@BookSearchActivity)
            }
        }

        setContent {
            EBookTheme(
                darkTheme = isDarkTheme()
            ) {
                BookSearchScreen(
                    bookSearchViewModel = bookSearchViewModel,
                    modifier = Modifier.fillMaxSize(),
                    onBookSearchItemClick = { book, paneNavigator ->
                        if (userPreferenceManager.isPreferCustomTab()) {
                            openInCustomTab(book.asUiModel().getLink())
                        } else {
                            paneNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail, book)
                        }

                        if (!hasUserSeenRankWindow) {
                            openResultCounts++
                        }
                    },
                    showAppBarCameraButton = isCameraAvailable(),
                    onAppBarCameraButtonPress = {
                        openCameraPreviewActivity()
                    },
                    onMenuSettingClick = {
                        openPreferenceActivity()
                    },
                    onShareOptionClick = { bookUiModel ->
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_SUBJECT, bookUiModel.getTitle())
                        intent.putExtra(Intent.EXTRA_TEXT, bookUiModel.getShareText())
                        startActivity(
                            Intent.createChooser(
                                intent,
                                getString(R.string.menu_share_menu_appear)
                            )
                        )
                    },
                    onOpenInBrowserClick = { bookUiModel ->
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(bookUiModel.getLink())
                        startActivity(intent)
                    },
                    checkShouldAskUserRankApp = {
                        checkShouldAskUserRankApp()
                    }
                )
            }
        }
        taskAfterViewCreated()
    }

    private fun taskAfterViewCreated() {
        // Render Book Result State
        lifecycleScope.launch {
            withStarted(block = {})
            bookSearchViewModel.viewState.collectLatest { state ->
                if (state != null) {
                    render(state)
                }
            }
        }

        // Render View Effect
        lifecycleScope.launch {
            hasUserSeenRankWindow = bookSearchViewModel.checkUserHasSeenRankWindow()
            bookSearchViewModel.screenViewState.collect {
                updateScreen(it)
            }
        }

        lifecycleScope.launch {
            withResumed {
                bookSearchViewModel.onViewReadyToServe()
                bookSearchViewModel.checkServiceStatus()
                handleInitialDeepLink()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent()
    }

    override fun onResume() {
        super.onResume()
        if (userPreferenceManager.isPreferCustomTab()) {
            checkShouldAskUserRankApp()
        }
    }

    private fun setupLauncherCallbacks() {
        changeThemeLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (isThemeChanged() || isStartToFollowSystemTheme()) {
                    recreate()
                }
                bookSearchViewModel.showSearchRecords(false)
            }
    }

    private fun handleIntent() {
        val searchKeyword = deeplinkHelper.getSearchKeyword(intent)
        val searchId = deeplinkHelper.getSearchId(intent)

        if (!searchKeyword.isNullOrEmpty()) {
            searchBook(searchKeyword)
        } else if (!searchId.isNullOrEmpty()) {
            showSearchSnapshot(searchId)
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

    private fun showSearchSnapshot(searchId: String) {
        hideVirtualKeyboard()
        bookSearchViewModel.requestSearchSnapshot(searchId)
    }

    private fun searchBook(keyword: String) {
        searchWithText(keyword)
    }

    private fun initAdMods() {
        MobileAds.initialize(this)
        val configurationBuilder = RequestConfiguration.Builder()
        if (BuildConfig.DEBUG) {
            configurationBuilder.setTestDeviceIds(listOf(BuildConfig.ADMOB_TEST_DEVICE_ID))
        }
        MobileAds.setRequestConfiguration(configurationBuilder.build())
    }

    @ColorInt
    private fun getThemePrimaryColor(): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.customTabHeaderColor, typedValue, true)
        return typedValue.data
    }

    private fun openCameraPreviewActivity() {
        barcodeScannerResultLauncher.launch(Intent(this, CameraPreviewActivity::class.java))
    }

    private fun isCameraAvailable(): Boolean = packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) ?: false

    private fun openPreferenceActivity() {
        val intent = Intent(this, PreferenceSettingsActivity::class.java)
        changeThemeLauncher.launch(intent)
    }

    private fun searchWithText(text: String) {
        changeSearchBoxKeyword(text)
        hideVirtualKeyboard()
        bookSearchViewModel.searchBook(text)
    }

    private fun changeSearchBoxKeyword(keyword: String) {
        bookSearchViewModel.updateKeyword(
            TextFieldValue(
                keyword,
                selection = TextRange(keyword.length)
            )
        )
        bookSearchViewModel.forceFocusOrUnfocusKeywordTextInput(false)
    }

    private fun hideVirtualKeyboard() {
        bookSearchViewModel.forceShowOrHideVirtualKeyboard(false)
    }

    private fun checkShouldAskUserRankApp() {
        lifecycleScope.launch {
            if (openResultCounts < POPUP_REVIEW_WINDOW_THRESHOLD) {
                return@launch
            }

            val hasUserSeenRankWindow =
                bookSearchViewModel.checkUserHasSeenRankWindow().also {
                    this@BookSearchActivity.hasUserSeenRankWindow = it
                }

            if (hasUserSeenRankWindow) {
                return@launch
            }

            if (BuildConfig.DEBUG) {
                Toast.makeText(
                    this@BookSearchActivity,
                    "Rank Window Should Popup",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val reviewInfo =
                runCatching {
                    playStoreReviewHelper.prepareReviewInfo()
                }.getOrNull()

            if (reviewInfo != null) {
                bookSearchViewModel.askUserRankApp(reviewInfo)
            }
        }
    }

    private fun shareCurrentPageSnapshot(url: String) {
        val intent = Intent(Intent.ACTION_SEND)
        with(intent) {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "From " + getString(R.string.app_name))
            putExtra(Intent.EXTRA_TEXT, url)
        }
        startActivity(
            Intent.createChooser(
                intent,
                getString(R.string.menu_share_menu_appear)
            )
        )
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
                lifecycleScope.launch {
                    playStoreReviewHelper.showReviewDialog(
                        this@BookSearchActivity,
                        screenState.reviewInfo
                    )
                    // Reset counts and flags
                    hasUserSeenRankWindow = true
                    openResultCounts = 0
                    bookSearchViewModel.rankAppWindowHasShown()
                }
            }
        }
    }

    private fun showToast(message: String) {
        message.showToastOn(this)
    }

    private fun showInternetRequestDialog() {
        val dialogBuilder = MaterialAlertDialogBuilder(this)
        dialogBuilder.setTitle(R.string.network_alert_dialog_title)
        dialogBuilder.setMessage(R.string.network_alert_message)
        dialogBuilder.setPositiveButton(R.string.dialog_ok) { _: DialogInterface, _: Int -> }
        dialogBuilder.create().show()
    }

    private fun showInternetConnectionTimeout() {
        showToast(getString(R.string.state_timeout))
    }

    private fun showKeywordIsEmpty() {
        showToast(getString(R.string.search_keyword_empty))
    }

    private fun showNetworkErrorMessage() {
        showToast(getString(R.string.network_error_message))
    }

    private fun openInCustomTab(url: String) {
        val colorParams =
            CustomTabColorSchemeParams.Builder()
                .setToolbarColor(getThemePrimaryColor())
                .build()
        val intent =
            CustomTabsIntent.Builder(customTabSessionManager.customTabsSession)
                .setShowTitle(true)
                .setDefaultColorSchemeParams(colorParams)
                .build()
        intent.launchUrl(this, Uri.parse(url))
    }

    //region BookResultViewState
    override fun render(viewState: BookResultViewState) {
        renderMainResultView(viewState)
    }

    private fun renderMainResultView(bookResultViewState: BookResultViewState) {
        when (bookResultViewState) {
            is BookResultViewState.PrepareBookResult -> {
                bookSearchViewModel.enableCameraButtonClick(false)
                bookSearchViewModel.enableSearchButtonClick(false)
                bookSearchViewModel.showCopyUrlOption(false)
                bookSearchViewModel.showShareSnapshotOption(false)
            }

            is BookResultViewState.ShowBooks -> {
                bookSearchViewModel.enableCameraButtonClick(true)
                bookSearchViewModel.enableSearchButtonClick(true)

                if (bookResultViewState.keyword.isNotEmpty()) {
                    changeSearchBoxKeyword(bookResultViewState.keyword)
                }

                bookSearchViewModel.showCopyUrlOption(true)
                bookSearchViewModel.showShareSnapshotOption(true)
            }

            BookResultViewState.PrepareBookResultError -> {
                bookSearchViewModel.enableCameraButtonClick(true)
                bookSearchViewModel.enableSearchButtonClick(true)
                bookSearchViewModel.showCopyUrlOption(false)
                bookSearchViewModel.showShareSnapshotOption(false)
            }

            is BookResultViewState.ShareCurrentPageSnapshot -> {
                shareCurrentPageSnapshot(bookResultViewState.url)
            }
        }
    }
    //endregion

    companion object {
        private const val POPUP_REVIEW_WINDOW_THRESHOLD = 5
        const val KEY_BARCODE_RESULT = "key-barcode-result"
    }
}
