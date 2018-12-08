package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import androidx.browser.customtabs.CustomTabsIntent
import androidx.preference.PreferenceManager
import liou.rayyuan.chromecustomtabhelper.ChromeCustomTabsHelper
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.camerapreview.CameraPreviewActivity
import liou.rayyuan.ebooksearchtaiwan.preferencesetting.PreferenceSettingsActivity
import liou.rayyuan.ebooksearchtaiwan.view.Router
import org.koin.android.ext.android.inject

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchActivity : BaseActivity(), ChromeCustomTabsHelper.Fallback {

    private val scanningBarcodeRequestCode = 1002
    private val preferenceSettingsRequestCode = 1003

    private val presenter: BookSearchPresenter by inject()
    private val router = Router(supportFragmentManager, R.id.activity_book_search_nav_host_container)
    private lateinit var chromeCustomTabHelper: ChromeCustomTabsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        chromeCustomTabHelper = ChromeCustomTabsHelper()

        val bookResultListFragment = router.findFragmentByTag(BookResultListFragment.TAG) as? BookResultListFragment
                ?: BookResultListFragment.newInstance()

        presenter.attachView(bookResultListFragment)
        router.replaceView(bookResultListFragment, BookResultListFragment.TAG)
    }

    override fun onResume() {
        super.onResume()
        chromeCustomTabHelper.bindCustomTabsServices(this, BuildConfig.HOST_URL)
    }

    override fun onStop() {
        super.onStop()
        chromeCustomTabHelper.unbindCustomTabsServices(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            scanningBarcodeRequestCode -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.extras?.run {
                        val resultText = getString(CameraPreviewActivity.resultISBNTextKey, "")
                        val bookResultFragment = router.findFragmentByTag(BookResultListFragment.TAG) as? BookResultListFragment
                        bookResultFragment?.searchWithText(resultText)
                    }
                }
            }
            preferenceSettingsRequestCode -> {
                if (isThemeChanged()) {
                    logThemeChangedEvent(isDarkTheme())
                    recreate()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
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
        val intent = Intent(this, CameraPreviewActivity::class.java)
        startActivityForResult(intent, scanningBarcodeRequestCode)
    }

    internal fun openPreferenceActivity() {
        val intent = Intent(this, PreferenceSettingsActivity::class.java)
        startActivityForResult(intent, preferenceSettingsRequestCode)
    }

    internal fun openBookLink(uri: Uri) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(getThemePrimaryColor())
        val chromeCustomTabIntent: CustomTabsIntent = builder.build()
        ChromeCustomTabsHelper.openCustomTab(this, chromeCustomTabIntent, uri, this)
    }

}