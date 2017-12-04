package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import liou.rayyuan.chromecustomtabhelper.ChromeCustomTabsHelper
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.view.ViewState
import liou.rayyuan.ebooksearchtaiwan.view.ViewState.ERROR
import liou.rayyuan.ebooksearchtaiwan.view.ViewState.PREPARE
import liou.rayyuan.ebooksearchtaiwan.view.ViewState.READY

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

    private val scrollView: NestedScrollView by bindView(R.id.search_view_scrollview)
    private val progressBar: ProgressBar by bindView(R.id.search_view_progressbar)

    lateinit var presenter: BookSearchPresenter
    lateinit var chromeCustomTabHelper: ChromeCustomTabsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        presenter = BookSearchPresenter()
        presenter.attachView(this)
        presenter.setBestResultRecyclerView(bestResultRecyclerView)
        presenter.setBookCompanyRecyclerView(bookCompanyRecyclerView)
        presenter.setReadmooRecyclerView(readmooRecyclerView)
        presenter.setKoboRecyclerView(koboRecyclerView)
        presenter.setTaazeRecyclerView(taazeRecyclerView)

        chromeCustomTabHelper = ChromeCustomTabsHelper()
        searchButton.setOnClickListener(this)
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
        bookCompanyTitle.text = resources.getString(R.string.booksCompanytTitle)
        readmooTitle.text = resources.getString(R.string.readmooTitle)
        koboTitle.text = resources.getString(R.string.koboTitle)
        taazeTitle.text = resources.getString(R.string.taazeTitle)
    }

    override fun openBookLink(uri: Uri) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
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
            }
            READY -> {
                progressBar.visibility = View.GONE
                scrollView.visibility = View.VISIBLE
            }
            ERROR -> {
                progressBar.visibility = View.GONE
                scrollView.visibility = View.INVISIBLE
            }
        }
    }

    override fun bookCompanyIsEmpty() {
    }

    override fun readmooIsEmpty() {
    }

    override fun koboIsEmpty() {
    }

    override fun taazeIsEmpty() {
    }
    //endregion

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.search_view_search_icon -> {
                val keyword: String = searchEditText.text.toString()
                presenter.searchBook(keyword)
            }
        }
    }

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
}