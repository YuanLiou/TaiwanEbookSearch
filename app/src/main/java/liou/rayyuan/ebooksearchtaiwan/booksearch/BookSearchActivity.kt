package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.app.Activity
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import liou.rayyuan.chromecustomtabhelper.ChromeCustomTabsHelper
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.view.ViewState
import liou.rayyuan.ebooksearchtaiwan.view.ViewState.*

/**
 * Created by louis383 on 2017/12/2.
 */
class BookSearchActivity : AppCompatActivity(), BookSearchView, View.OnClickListener, ChromeCustomTabsHelper.Fallback {

    private val searchButton: ImageView by bindView(R.id.search_view_search_icon)
    private val searchEditText: EditText by bindView(R.id.search_view_edittext)

    private val resultsRecyclerView: RecyclerView by bindView(R.id.search_view_result)
    private val progressBar: ProgressBar by bindView(R.id.search_view_progressbar)

    private val hintText: TextView by bindView(R.id.search_view_hint)
    private val shadow: View by bindView(R.id.search_view_shadow)
    private val adView: AdView by bindView(R.id.search_view_ad)

    private lateinit var presenter: BookSearchPresenter
    private lateinit var chromeCustomTabHelper: ChromeCustomTabsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        presenter = BookSearchPresenter()
        presenter.attachView(this)

        MobileAds.initialize(this, BuildConfig.AD_MOB_ID)

        chromeCustomTabHelper = ChromeCustomTabsHelper()
        searchButton.setOnClickListener(this)
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

    //region BookSearchView
    override fun setupUI() {
        val hintWithAppVersion = hintText.text.toString() + "\n" + resources.getString(R.string.app_version, BuildConfig.VERSION_NAME)
        hintText.text = hintWithAppVersion

        val adRequestBuilder = AdRequest.Builder()
        val adRequest = adRequestBuilder.build()
        adView.loadAd(adRequest)
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
        dialogBuilder.setPositiveButton(R.string.dialog_ok, { _: DialogInterface, _: Int -> })
        dialogBuilder.create().show()
    }

    override fun showInternetConnectionTimeout() {
        Toast.makeText(this, R.string.state_timeout, Toast.LENGTH_LONG).show()
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
                shadow.visibility = View.GONE
                adView.visibility = View.INVISIBLE
            }
            READY -> {
                progressBar.visibility = View.GONE
                resultsRecyclerView.visibility = View.VISIBLE
                hintText.visibility = View.GONE
                shadow.visibility = View.VISIBLE
//                adView.visibility = View.VISIBLE
            }
            ERROR -> {
                progressBar.visibility = View.GONE
                resultsRecyclerView.visibility = View.GONE
                hintText.visibility = View.VISIBLE
                shadow.visibility = View.GONE
                adView.visibility = View.INVISIBLE
            }
        }
    }

    override fun showErrorMessage(message: String) {
        val errorDrawable: Drawable = ContextCompat.getDrawable(this, R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
        DrawableCompat.setTint(errorDrawable, ContextCompat.getColor(this, R.color.gray))
        hintText.setOnClickListener(null)
        hintText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, errorDrawable, null, null)
        hintText.text = message
    }

    override fun hideVirtualKeyboard() {
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun showVirtualKeyboard() {
        searchEditText.requestFocus()
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(searchEditText, 0)
    }

    override fun getApplicationString(stringId: Int): String {
        return resources.getString(stringId)
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
        Toast.makeText(this, getString(R.string.network_error_message), Toast.LENGTH_LONG).show()
    }
    //endregion

    //region View.OnClickListener
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.search_view_search_icon -> {
                val keyword: String = searchEditText.text.toString()
                presenter.searchBook(keyword)
            }
            R.id.search_view_hint -> { presenter.hintPressed() }
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

    private fun <T: View> Activity.bindView(@IdRes resId: Int): Lazy<T> =
            lazy { findViewById<T>(resId) }

    private fun getThemePrimaryColor(): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }
}