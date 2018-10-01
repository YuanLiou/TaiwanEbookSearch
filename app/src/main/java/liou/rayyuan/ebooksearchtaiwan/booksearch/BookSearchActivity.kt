package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_search.*
import liou.rayyuan.chromecustomtabhelper.ChromeCustomTabsHelper
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.camerapreview.CameraPreviewActivity
import liou.rayyuan.ebooksearchtaiwan.model.RemoteConfigManager
import liou.rayyuan.ebooksearchtaiwan.preferencesetting.PreferenceSettingsActivity
import liou.rayyuan.ebooksearchtaiwan.utils.bindView
import liou.rayyuan.ebooksearchtaiwan.utils.showToastOn
import liou.rayyuan.ebooksearchtaiwan.view.ViewState
import liou.rayyuan.ebooksearchtaiwan.view.ViewState.*
import org.koin.android.ext.android.inject

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchActivity : BaseActivity(), BookSearchView, View.OnClickListener, ChromeCustomTabsHelper.Fallback {

    private val scanningBarcodeRequestCode = 1002
    private val preferenceSettingsRequestCode = 1003

    private val appbar: AppBarLayout by bindView(R.id.search_view_appbar)
    private val adViewLayout: FrameLayout by bindView(R.id.search_view_adview_layout)
    private val searchButton: ImageView by bindView(R.id.search_view_search_icon)
    private val cameraButton: ImageView by bindView(R.id.search_view_camera_icon)
    private val searchEditText: EditText by bindView(R.id.search_view_edittext)

    private val resultsRecyclerView: RecyclerView by bindView(R.id.search_view_result)
    private val progressBar: ProgressBar by bindView(R.id.search_view_progressbar)

    private val hintText: TextView by bindView(R.id.search_view_hint)
    private val backToTopButton: ImageButton by bindView(R.id.search_view_back_to_top_button)

    private val presenter: BookSearchPresenter by inject()
    private lateinit var chromeCustomTabHelper: ChromeCustomTabsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        setSupportActionBar(search_view_toolbar)
        presenter.attachView(this)

        chromeCustomTabHelper = ChromeCustomTabsHelper()
        searchButton.setOnClickListener(this)
        if (isCameraAvailable()) {
            cameraButton.setOnClickListener(this)
        } else {
            cameraButton.visibility = View.GONE
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val keyword: String = searchEditText.text.toString()
                presenter.searchBook(keyword)
            }
            false
        }
        hintText.setOnClickListener(this)
        hintText.compoundDrawables
                .filterNotNull()
                .forEach { DrawableCompat.setTint(it, ContextCompat.getColor(this, R.color.gray)) }

        val linearLayoutManager: LinearLayoutManager = resultsRecyclerView.layoutManager as LinearLayoutManager
        linearLayoutManager.initialPrefetchItemCount = 6

        loadAds()
        initScrollToTopButton()
        presenter.setResultRecyclerView(resultsRecyclerView)
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
                        searchEditText.setText(resultText)
                        searchEditText.setSelection(resultText.length)
                        presenter.searchBook(resultText)
                        presenter.logISBNScanningSucceed()
                    }
                }
            }
            preferenceSettingsRequestCode -> {
                if (isThemeChanged()) {
                    presenter.logThemeChangedEvent(isDarkTheme())
                    recreate()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_page, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            return when (it.itemId) {
                R.id.search_page_menu_action_setting -> {
                    val intent = Intent(this, PreferenceSettingsActivity::class.java)
                    startActivityForResult(intent, preferenceSettingsRequestCode)
                    true
                }
                else -> true
            }
        } ?: return super.onOptionsItemSelected(item)
    }

    private fun loadAds() {
        val adView: AdView = adViewLayout.findViewById(R.id.admob_view_header_adview)
        val adRequestBuilder = AdRequest.Builder()
        val adRequest = adRequestBuilder.build()
        adView.loadAd(adRequest)
    }

    private fun initScrollToTopButton() {
        backToTopButton.setOnClickListener(this)

        if (remoteConfigManager.firebaseRemoteConfig.getBoolean(RemoteConfigManager.COLOR_BACK_TO_TOP_BUTTON_KEY)) {
            backToTopButton.setBackgroundResource(R.drawable.material_rounded_button_green)
        } else {
            val typedValue = TypedValue()
            theme.resolveAttribute(R.attr.backToTopButtonDrawable, typedValue, true)
            val backToButtonButtonResourceId = typedValue.resourceId
            backToTopButton.setBackgroundResource(backToButtonButtonResourceId)
        }

        resultsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (resultsRecyclerView.canScrollVertically(-1)) {
                    backToTopButton.setImageResource(baseContext, R.drawable.ic_keyboard_arrow_up_24dp)
                } else {

                    if (remoteConfigManager.firebaseRemoteConfig.getBoolean(RemoteConfigManager.KEYBOARD_BACK_TO_TOP_ICON_KEY)) {
                        backToTopButton.setImageResource(baseContext, R.drawable.ic_keyboard_white_24dp)
                    } else {
                        backToTopButton.setImageResource(baseContext, R.drawable.ic_search_white_24dp)
                    }
                }
            }
        })
    }

    //region BookSearchView
    override fun setupUI() {
        val hintWithAppVersion = hintText.text.toString() + "\n" + resources.getString(R.string.app_version, BuildConfig.VERSION_NAME)
        hintText.text = hintWithAppVersion
    }

    override fun openBookLink(uri: Uri) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(getThemePrimaryColor())
        val chromeCustomTabIntent: CustomTabsIntent = builder.build()
        ChromeCustomTabsHelper.openCustomTab(this, chromeCustomTabIntent, uri, this)
    }

    override fun isInternetConnectionAvailable(): Boolean {
        val connectionManager: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectionManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected && networkInfo.isAvailable
    }

    override fun showInternetRequestDialog() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(R.string.network_alert_dialog_title)
        dialogBuilder.setMessage(R.string.network_alert_message)
        dialogBuilder.setPositiveButton(R.string.dialog_ok) { _: DialogInterface, _: Int -> }
        dialogBuilder.create().show()
    }

    override fun showInternetConnectionTimeout() {
        getString(R.string.state_timeout).showToastOn(this)
    }

    override fun scrollToTop() {
        resultsRecyclerView.scrollToPosition(0)
    }

    override fun setMainResultView(viewState: ViewState) {
        when (viewState) {
            PREPARE -> {
                progressBar.visibility = View.VISIBLE
                resultsRecyclerView.visibility = View.GONE
                hintText.visibility = View.GONE
                backToTopButton.visibility = View.GONE
                adViewLayout.visibility = View.VISIBLE
            }
            READY -> {
                progressBar.visibility = View.GONE
                resultsRecyclerView.visibility = View.VISIBLE
                hintText.visibility = View.GONE
                backToTopButton.visibility = View.VISIBLE
                adViewLayout.visibility = View.GONE
            }
            ERROR -> {
                progressBar.visibility = View.GONE
                resultsRecyclerView.visibility = View.GONE
                hintText.visibility = View.VISIBLE
                backToTopButton.visibility = View.GONE
                adViewLayout.visibility = View.GONE
            }
        }
    }

    override fun showErrorMessage(message: String) {
        val errorDrawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
        errorDrawable?.let { DrawableCompat.setTint(it, ContextCompat.getColor(this, R.color.gray)) }
        hintText.setOnClickListener(null)
        hintText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, errorDrawable, null, null)
        hintText.text = message
    }

    override fun hideVirtualKeyboard() {
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun focusBookSearchEditText() {
        appbar.setExpanded(true, true)
        searchEditText.requestFocus()
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(searchEditText, 0)
    }

    override fun showKeywordIsEmpty() {
        Toast.makeText(this, R.string.search_keyword_empty, Toast.LENGTH_LONG).show()
    }

    override fun showEasterEgg01() {
        Toast.makeText(this, R.string.easter_egg_01, Toast.LENGTH_LONG).show()
    }

    override fun getViewModelProvider(): ViewModelProvider {
        return ViewModelProviders.of(this)
    }

    override fun getLifeCycleOwner(): LifecycleOwner {
        return this
    }

    override fun showNetworkErrorMessage() {
        getString(R.string.network_error_message).showToastOn(this)
    }

    override fun backToListTop() {
        resultsRecyclerView.smoothScrollToPosition(0)
    }

    override fun showToast(message: String) {
        message.showToastOn(this)
    }

    //endregion

    //region View.OnClickListener
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.search_view_search_icon -> {
                val keyword: String = searchEditText.text.toString()
                presenter.searchBook(keyword)
            }
            R.id.search_view_hint -> presenter.hintPressed()
            R.id.search_view_camera_icon -> {
                val intent = Intent(this, CameraPreviewActivity::class.java)
                startActivityForResult(intent, scanningBarcodeRequestCode)
            }
            R.id.search_view_back_to_top_button -> {
                val canListScrollVertically = resultsRecyclerView.canScrollVertically(-1)
                presenter.backToTop(canListScrollVertically)
            }
        }
    }
    //endregion

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

    private fun Activity.isCameraAvailable(): Boolean {
        return this.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    private fun ImageButton.setImageResource(context: Context, @DrawableRes drawableId: Int) {
        ContextCompat.getDrawable(context, drawableId)?.run {
            if (isDarkTheme()) {
                DrawableCompat.setTint(this, ContextCompat.getColor(baseContext, R.color.pure_dark))
            }
            this@setImageResource.setImageDrawable(this)
        }
    }
}