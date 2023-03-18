package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.rayliu.commonmain.domain.model.Book
import kotlinx.coroutines.launch
import liou.rayyuan.chromecustomtabhelper.ChromeCustomTabsHelper
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.camerapreview.CameraPreviewActivity
import liou.rayyuan.ebooksearchtaiwan.model.DeeplinkHelper
import liou.rayyuan.ebooksearchtaiwan.preferencesetting.PreferenceSettingsActivity
import liou.rayyuan.ebooksearchtaiwan.simplewebview.SimpleWebViewFragment
import liou.rayyuan.ebooksearchtaiwan.utils.QuickChecker
import liou.rayyuan.ebooksearchtaiwan.utils.showToastMessage
import liou.rayyuan.ebooksearchtaiwan.view.Router
import org.koin.android.ext.android.inject

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchActivity :
    BaseActivity(R.layout.activity_book_search),
    ChromeCustomTabsHelper.Fallback,
    SimpleWebViewFragment.OnSimpleWebViewActionListener {

    private val KEY_LAST_FRAGMENT_TAG = "key-last-fragment-tag"

    private val quickChecker: QuickChecker by inject()
    private val deeplinkHelper = DeeplinkHelper()
    private var isDualPane: Boolean = false
    private lateinit var contentRouter: Router
    private var dualPaneSubRouter: Router? = null
    private lateinit var chromeCustomTabHelper: ChromeCustomTabsHelper

    private lateinit var barcodeScanningLauncher: ActivityResultLauncher<ScanOptions>
    private lateinit var changeThemeLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        chromeCustomTabHelper = ChromeCustomTabsHelper()

        val secondContainer: View? = findViewById(R.id.activity_book_search_content_container)
        isDualPane = secondContainer?.visibility == View.VISIBLE

        contentRouter = if (isDualPane) {
            Router(supportFragmentManager, R.id.activity_book_search_content_container)
        } else {
            Router(supportFragmentManager, R.id.activity_book_search_nav_host_container)
        }

        setupBackGesture()
        setupLauncherCallbacks()

        if (savedInstanceState == null) {
            val appLinkKeyword = deeplinkHelper.getSearchKeyword(intent)
            val appLinkSnapshotSearchId = deeplinkHelper.getSearchId(intent)
            val bookResultListFragment = BookResultListFragment.newInstance(appLinkKeyword, appLinkSnapshotSearchId)
            if (isDualPane) {
                dualPaneSubRouter = Router(
                    supportFragmentManager,
                    R.id.activity_book_search_nav_host_container
                ).also {
                    it.addView(bookResultListFragment, BookResultListFragment.TAG, false)
                }
            } else {
                contentRouter.addView(bookResultListFragment, BookResultListFragment.TAG, false)
            }
        } else {
            if (savedInstanceState.getString(KEY_LAST_FRAGMENT_TAG) != null) {
                val lastFragmentTag = savedInstanceState.getString(KEY_LAST_FRAGMENT_TAG) ?: return
                val lastFragment = contentRouter.findFragmentByTag(lastFragmentTag)
                (lastFragment as? SimpleWebViewFragment)?.onSimpleWebViewActionListener = this
            }
        }

        if (!userPreferenceManager.isPreferCustomTab()) {
            lifecycleScope.launch {
                contentRouter.backStackCountsPublisher().collect { backStackCounts ->
                    if (backStackCounts == 0) {
                        checkShouldAskUserRankApp()
                    }
                }
            }
        }
    }

    private fun setupLauncherCallbacks() {
        barcodeScanningLauncher = registerForActivityResult(ScanContract()) { result ->
            if (result.contents == null) {
                val originalIntent = result.originalIntent
                if (originalIntent?.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION) == true) {
                    showToastMessage(R.string.permission_required_camera)
                }
            } else {
                val resultText = result.contents
                val bookResultFragment = getBookResultFragment()
                bookResultFragment?.searchWithText(resultText)
            }
        }

        changeThemeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (isThemeChanged() || isStartToFollowSystemTheme()) {
                recreate()
            }
            getBookResultFragment()?.toggleSearchRecordView(false)
        }
    }

    override fun onNewIntent(intent: Intent?) {
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
        if (isDualPane) {
            val bookSearchFragment = dualPaneSubRouter?.findFragmentByTag(BookResultListFragment.TAG) as? BookResultListFragment
            bookSearchFragment?.showSearchSnapshot(searchId)
        } else {
            val bookSearchFragment = contentRouter.findFragmentByTag(BookResultListFragment.TAG) as? BookResultListFragment
            bookSearchFragment?.showSearchSnapshot(searchId)
        }
    }

    private fun searchBook(keyword: String) {
        if (isDualPane) {
            val bookSearchFragment = dualPaneSubRouter?.findFragmentByTag(BookResultListFragment.TAG) as? BookResultListFragment
            bookSearchFragment?.searchWithText(keyword)
        } else {
            val bookSearchFragment = contentRouter.findFragmentByTag(BookResultListFragment.TAG) as? BookResultListFragment
            bookSearchFragment?.searchWithText(keyword)
        }
    }

    override fun onResume() {
        super.onResume()
        if (userPreferenceManager.isPreferCustomTab()) {
            chromeCustomTabHelper.bindCustomTabsServices(
                this,
                userPreferenceManager.getPreferBrowser(),
                "https://taiwan-ebook-lover.github.io"
            )
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

    override fun onStop() {
        super.onStop()
        if (userPreferenceManager.isPreferCustomTab()) {
            chromeCustomTabHelper.unbindCustomTabsServices(this)
        }
    }

    private fun setupBackGesture() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressed()
            }
        })
    }

    //region ChromeCustomTabsHelper.Fallback
    override fun openWithWebView(activity: Activity?, uri: Uri?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
        startActivity(intent)
    }
    //endregion

    private fun checkShouldAskUserRankApp() {
        if (isDualPane) {
            val bookSearchFragment =
                dualPaneSubRouter?.findFragmentByTag(BookResultListFragment.TAG) as? BookResultListFragment
            bookSearchFragment?.checkShouldAskUserRankApp()
        } else {
            val bookSearchFragment =
                contentRouter.findFragmentByTag(BookResultListFragment.TAG) as? BookResultListFragment
            bookSearchFragment?.checkShouldAskUserRankApp()
        }
    }

    private fun getThemePrimaryColor(): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }

    internal fun openCameraPreviewActivity() {
        val scanOptions = ScanOptions()
            .setOrientationLocked(false)
            .setDesiredBarcodeFormats(ScanOptions.EAN_13)
            .setCaptureActivity(CameraPreviewActivity::class.java)
        barcodeScanningLauncher.launch(scanOptions)
    }

    internal fun openPreferenceActivity() {
        val intent = Intent(this, PreferenceSettingsActivity::class.java)
        changeThemeLauncher.launch(intent)
    }

    internal fun openBookLink(book: Book) {
        if (!quickChecker.isTabletSize() && userPreferenceManager.isPreferCustomTab()) {
            val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(getThemePrimaryColor())
            val chromeCustomTabIntent: CustomTabsIntent = builder.build()
            ChromeCustomTabsHelper.openCustomTab(
                this, userPreferenceManager.getPreferBrowser(),
                chromeCustomTabIntent, Uri.parse(book.link), this
            )
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

    private fun getBookResultFragment(): BookResultListFragment? {
        return if (isDualPane) {
            dualPaneSubRouter?.findFragmentByTag(BookResultListFragment.TAG) as? BookResultListFragment
        } else {
            contentRouter.findFragmentByTag(BookResultListFragment.TAG) as? BookResultListFragment
        }
    }

    //region SimpleWebViewFragment.OnSimpleWebviewActionListener
    override fun onSimpleWebViewClose(tag: String) {
        contentRouter.backToPreviousFragment()
    }
    //endregion
}
