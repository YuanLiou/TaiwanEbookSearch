package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
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

    private val bestResultTitle: TextView by bindView(R.id.search_result_subtitle_top)
    private val bestResultRecyclerView: RecyclerView by bindView(R.id.search_result_list_top)

    private val bookCompanyTitle: TextView by bindView(R.id.search_result_subtitle_store1)
    private val bookCompanyRecyclerView: RecyclerView by bindView(R.id.search_result_list_store1)

    private val readmooTitle: TextView by bindView(R.id.search_result_subtitle_store2)
    private val readmooRecyclerView: RecyclerView by bindView(R.id.search_result_list_store2)

    private val koboTitle: TextView by bindView(R.id.search_result_subtitle_store3)
    private val koboRecyclerView: RecyclerView by bindView(R.id.search_result_list_store3)

    private val taazeTitle: TextView by bindView(R.id.search_result_subtitle_store4)
    private val taazeRecyclerView: RecyclerView by bindView(R.id.search_result_list_store4)

    private val bookWalkerTitle: TextView by bindView(R.id.search_result_subtitle_store5)
    private val bookWalkerRecyclerView: RecyclerView by bindView(R.id.search_result_list_store5)

    private val playBooksTitle: TextView by bindView(R.id.search_result_subtitle_store6)
    private val playBooksRecyclerView: RecyclerView by bindView(R.id.search_result_list_store6)

    private val scrollView: NestedScrollView by bindView(R.id.search_view_scrollview)
    private val progressBar: ProgressBar by bindView(R.id.search_view_progressbar)

    private val hintText: TextView by bindView(R.id.search_view_hint)

    lateinit var presenter: BookSearchPresenter
    lateinit var chromeCustomTabHelper: ChromeCustomTabsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        presenter = BookSearchPresenter()
        presenter.attachView(this)
        setRecyclerViews()

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
        for (drawable in hintText.compoundDrawables) {
            if (drawable != null) {
                DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.gray))
            }
        }
    }

    private fun setRecyclerViews() {
        presenter.setBestResultRecyclerView(bestResultRecyclerView)
        presenter.setBookCompanyRecyclerView(bookCompanyRecyclerView)
        presenter.setReadmooRecyclerView(readmooRecyclerView)
        presenter.setKoboRecyclerView(koboRecyclerView)
        presenter.setTaazeRecyclerView(taazeRecyclerView)
        presenter.setBookWalkerRecyclerView(bookWalkerRecyclerView)
        presenter.setPlayBooksRecycylerView(playBooksRecyclerView)
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
    override fun setupInterface() {
        bestResultTitle.text = resources.getString(R.string.best_result_title)
        bookCompanyTitle.text = resources.getString(R.string.books_companyt_title)
        readmooTitle.text = resources.getString(R.string.readmoo_title)
        koboTitle.text = resources.getString(R.string.kobo_title)
        taazeTitle.text = resources.getString(R.string.taaze_title)
        bookWalkerTitle.text = resources.getString(R.string.book_walker_title)
        playBooksTitle.text = resources.getString(R.string.playbook_title)
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
        scrollView.smoothScrollTo(0, 0)
    }

    override fun setMainResultView(viewState: ViewState) {
        when (viewState) {
            PREPARE -> {
                progressBar.visibility = View.VISIBLE
                scrollView.visibility = View.INVISIBLE
                hintText.visibility = View.GONE
            }
            READY -> {
                progressBar.visibility = View.GONE
                scrollView.visibility = View.VISIBLE
                hintText.visibility = View.GONE
            }
            ERROR -> {
                progressBar.visibility = View.GONE
                scrollView.visibility = View.INVISIBLE
                hintText.visibility = View.VISIBLE
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

    override fun bookCompanyIsEmpty() {}

    override fun readmooIsEmpty() {}

    override fun koboIsEmpty() {}

    override fun taazeIsEmpty() {}

    override fun bookWalkerIsEmpty() {}

    override fun playBookIsEmpty() {}

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

    private fun <T: View> Activity.bindView(@IdRes resId: Int): Lazy<T> {
        return lazy { findViewById<T>(resId) }
    }

    private fun getThemePrimaryColor(): Int {
        val typedValue: TypedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        @ColorInt val colorId: Int = typedValue.data
        return colorId
    }
}