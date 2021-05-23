package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.preference.PreferenceManager
import com.google.zxing.client.android.Intents
import com.google.zxing.integration.android.IntentIntegrator
import liou.rayyuan.chromecustomtabhelper.ChromeCustomTabsHelper
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.camerapreview.CameraPreviewActivity
import liou.rayyuan.ebooksearchtaiwan.model.DeeplinkHelper
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.preferencesetting.PreferenceSettingsActivity
import liou.rayyuan.ebooksearchtaiwan.simplewebview.SimpleWebViewFragment
import liou.rayyuan.ebooksearchtaiwan.utils.QuickChecker
import liou.rayyuan.ebooksearchtaiwan.utils.showToastMessage
import liou.rayyuan.ebooksearchtaiwan.view.Router
import org.koin.android.ext.android.inject

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchActivity : BaseActivity(R.layout.activity_book_search), ChromeCustomTabsHelper.Fallback,
        SimpleWebViewFragment.OnSimpleWebviewActionListener {

    private val KEY_LAST_FRAGMENT_TAG = "key-last-fragment-tag"
    private val scanningBarcodeRequestCode = 1002
    private val preferenceSettingsRequestCode = 1003

    private val quickChecker: QuickChecker by inject()
    private val deeplinkHelper = DeeplinkHelper()
    private var isDualPane: Boolean = false
    private lateinit var contentRouter: Router
    private lateinit var chromeCustomTabHelper: ChromeCustomTabsHelper

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

        if (savedInstanceState == null) {
            val appLinkKeyword = deeplinkHelper.getSearchKeyword(intent)
            val bookResultListFragment = BookResultListFragment.newInstance(appLinkKeyword)
            if (isDualPane) {
                val subRouter = Router(
                    supportFragmentManager,
                    R.id.activity_book_search_nav_host_container
                )
                subRouter.addView(bookResultListFragment, BookResultListFragment.TAG, false)
            } else {
                contentRouter.addView(bookResultListFragment, BookResultListFragment.TAG, false)
            }
        } else {
            if (savedInstanceState.getString(KEY_LAST_FRAGMENT_TAG) != null) {
                val lastFragmentTag = savedInstanceState.getString(KEY_LAST_FRAGMENT_TAG) ?: return
                val lastFragment = contentRouter.findFragmentByTag(lastFragmentTag)
                (lastFragment as? SimpleWebViewFragment)?.onSimpleWebviewActionListener = this
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent()
    }

    private fun handleIntent() {
        val searchKeyword = deeplinkHelper.getSearchKeyword(intent)
        Log.i("BookSearchActivity", "Search Keyword is = $searchKeyword")
        searchKeyword?.run {
            if (isDualPane) {
                val subRouter = Router(
                    supportFragmentManager,
                    R.id.activity_book_search_nav_host_container
                )
                val bookSearchFragment = subRouter.findFragmentByTag(BookResultListFragment.TAG) as? BookResultListFragment
                bookSearchFragment?.searchWithText(searchKeyword)
            } else {
                val bookSearchFragment = contentRouter.findFragmentByTag(BookResultListFragment.TAG) as? BookResultListFragment
                bookSearchFragment?.searchWithText(searchKeyword)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (userPreferenceManager.isPreferCustomTab()) {
            chromeCustomTabHelper.bindCustomTabsServices(
                this,
                userPreferenceManager.getPreferBrowser(),
                BuildConfig.HOST_URL
            )
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            scanningBarcodeRequestCode -> {
                val result = IntentIntegrator.parseActivityResult(resultCode, data)
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
            preferenceSettingsRequestCode -> {
                if (isThemeChanged() || isStartToFollowSystemTheme()) {
                    recreate()
                }
                getBookResultFragment()?.toggleSearchRecordView(false)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //region ChromeCustomTabsHelper.Fallback
    override fun openWithWebView(activity: Activity?, uri: Uri?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
        startActivity(intent)
    }
    //endregion

    private fun getThemePrimaryColor(): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }

    internal fun openCameraPreviewActivity() {
        IntentIntegrator(this)
            .setOrientationLocked(false)
            .setDesiredBarcodeFormats(IntentIntegrator.EAN_13)
            .setRequestCode(scanningBarcodeRequestCode)
            .setCaptureActivity(CameraPreviewActivity::class.java)
            .initiateScan()
    }

    internal fun openPreferenceActivity() {
        val intent = Intent(this, PreferenceSettingsActivity::class.java)
        startActivityForResult(intent, preferenceSettingsRequestCode)
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
            val resultFragment = contentRouter.findFragmentByTag(SimpleWebViewFragment.TAG) as? SimpleWebViewFragment
            resultFragment?.loadBookResult(book) ?: run {
                val webViewFragment = SimpleWebViewFragment.newInstance(book, !isTablet)
                webViewFragment.onSimpleWebviewActionListener = this
                contentRouter.addView(webViewFragment, SimpleWebViewFragment.TAG, true)
            }
        }
    }

    override fun onBackPressed() {
        if (contentRouter.findTopFragment() is SimpleWebViewFragment) {
            val canGoBack = (contentRouter.findTopFragment() as SimpleWebViewFragment).goBack()
            if (canGoBack) {
                return
            }
        }

        getBookResultFragment()?.let {
            val isConsumed = it.onBackPressed()
            if (isConsumed) {
                return
            }
        }

        if (!contentRouter.backToPreviousFragment()) {
            super.onBackPressed()
        }
    }

    private fun getBookResultFragment(): BookResultListFragment? {
        return if (isDualPane) {
            val subRouter = Router(
                supportFragmentManager,
                R.id.activity_book_search_nav_host_container
            )
            subRouter.findFragmentByTag(BookResultListFragment.TAG) as? BookResultListFragment
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