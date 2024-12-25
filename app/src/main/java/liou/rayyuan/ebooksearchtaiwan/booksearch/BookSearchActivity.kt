package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.rayliu.commonmain.domain.model.Book
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.camerapreview.CameraPreviewActivity
import liou.rayyuan.ebooksearchtaiwan.model.DeeplinkHelper
import liou.rayyuan.ebooksearchtaiwan.preferencesetting.PreferenceSettingsActivity
import liou.rayyuan.ebooksearchtaiwan.simplewebview.SimpleWebViewFragment
import liou.rayyuan.ebooksearchtaiwan.utils.CustomTabSessionManager
import liou.rayyuan.ebooksearchtaiwan.utils.QuickChecker
import liou.rayyuan.ebooksearchtaiwan.view.Router
import org.koin.android.ext.android.inject

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchActivity :
    BaseActivity(R.layout.activity_book_search),
    SimpleWebViewFragment.OnSimpleWebViewActionListener {
    private val quickChecker: QuickChecker by inject()
    private val customTabSessionManager: CustomTabSessionManager by inject()
    private val deeplinkHelper = DeeplinkHelper()
    private lateinit var contentRouter: Router

    private lateinit var changeThemeLauncher: ActivityResultLauncher<Intent>

    private val barcodeScannerResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val bundle = activityResult.data?.extras
                val resultText = bundle?.getString(KEY_BARCODE_RESULT, "").orEmpty()
                if (resultText.isNotEmpty()) {
                    val bookResultFragment = getBookResultFragment()
                    bookResultFragment?.searchWithText(resultText)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        contentRouter = Router(supportFragmentManager, R.id.activity_book_search_nav_host_container)
        setupBackGesture()
        setupLauncherCallbacks()

        if (savedInstanceState == null) {
            val appLinkKeyword = deeplinkHelper.getSearchKeyword(intent)
            val appLinkSnapshotSearchId = deeplinkHelper.getSearchId(intent)
            val bookResultListFragment =
                BookResultListFragment.newInstance(
                    appLinkKeyword,
                    appLinkSnapshotSearchId
                )
            contentRouter.addView(bookResultListFragment, BookResultListFragment.TAG, false)
        } else {
            if (savedInstanceState.getString(KEY_LAST_FRAGMENT_TAG) != null) {
                val lastFragmentTag = savedInstanceState.getString(KEY_LAST_FRAGMENT_TAG) ?: return
                val lastFragment = contentRouter.findFragmentByTag(lastFragmentTag)
                (lastFragment as? SimpleWebViewFragment)?.onSimpleWebViewActionListener = this
            }
        }

        lifecycleScope.launch {
            if (userPreferenceManager.isPreferCustomTab()) {
                customTabSessionManager.bindCustomTabService(this@BookSearchActivity)
            } else {
                contentRouter.backStackCountsPublisher().collect { backStackCounts ->
                    if (backStackCounts == 0) {
                        checkShouldAskUserRankApp()
                    }
                }
            }
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
                getBookResultFragment()?.toggleSearchRecordView(false)
            }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent()
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

    private fun showSearchSnapshot(searchId: String) {
        getBookResultFragment()?.showSearchSnapshot(searchId)
    }

    private fun searchBook(keyword: String) {
        getBookResultFragment()?.searchWithText(keyword)
    }

    override fun onResume() {
        super.onResume()
        if (userPreferenceManager.isPreferCustomTab()) {
            checkShouldAskUserRankApp()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val topFragmentTag = contentRouter.findTopFragment()?.tag
        if (topFragmentTag != null) {
            outState.putString(KEY_LAST_FRAGMENT_TAG, topFragmentTag)
        }
    }

    private fun setupBackGesture() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backPressed()
                }
            }
        )
    }

    private fun checkShouldAskUserRankApp() {
        getBookResultFragment()?.checkShouldAskUserRankApp()
    }

    @ColorInt
    private fun getThemePrimaryColor(): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.customTabHeaderColor, typedValue, true)
        return typedValue.data
    }

    fun openCameraPreviewActivity() {
        barcodeScannerResultLauncher.launch(Intent(this, CameraPreviewActivity::class.java))
    }

    fun openPreferenceActivity() {
        val intent = Intent(this, PreferenceSettingsActivity::class.java)
        changeThemeLauncher.launch(intent)
    }

    fun openBookLink(book: Book) {
        if (!quickChecker.isTabletSize() && userPreferenceManager.isPreferCustomTab()) {
            val colorParams =
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(getThemePrimaryColor())
                    .build()
            val intent =
                CustomTabsIntent.Builder(customTabSessionManager.customTabsSession)
                    .setShowTitle(true)
                    .setDefaultColorSchemeParams(colorParams)
                    .build()
            intent.launchUrl(this, Uri.parse(book.link))
        } else {
            val isTablet = quickChecker.isTabletSize()
            val webViewFragment = SimpleWebViewFragment.newInstance(book, !isTablet)
            webViewFragment.onSimpleWebViewActionListener = this
            contentRouter.addView(webViewFragment, SimpleWebViewFragment.TAG + book.id, true)
        }
    }

    private fun backPressed() {
        if (contentRouter.findTopFragment() is SimpleWebViewFragment) {
            val canGoBack = (contentRouter.findTopFragment() as SimpleWebViewFragment).goBack()
            if (canGoBack) {
                return
            }
        }

        getBookResultFragment()?.let {
            val isConsumed = it.backPressed()
            if (isConsumed) {
                return
            }
        }

        if (!contentRouter.backToPreviousFragment()) {
            finish()
        }
    }

    private fun getBookResultFragment(): BookResultListFragment? =
        contentRouter.findFragmentByTag(BookResultListFragment.TAG) as? BookResultListFragment

    //region SimpleWebViewFragment.OnSimpleWebviewActionListener
    override fun onSimpleWebViewClose(tag: String) {
        contentRouter.backToPreviousFragment()
    }
    //endregion

    companion object {
        private const val KEY_LAST_FRAGMENT_TAG = "key-last-fragment-tag"
        const val KEY_BARCODE_RESULT = "key-barcode-result"
    }
}
