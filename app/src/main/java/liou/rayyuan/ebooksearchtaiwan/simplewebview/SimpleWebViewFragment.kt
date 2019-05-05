package liou.rayyuan.ebooksearchtaiwan.simplewebview

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.ShareActionProvider
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import kotlinx.android.synthetic.main.fragment_simple_webview.*
import liou.rayyuan.ebooksearchtaiwan.BaseFragment
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.BookView
import liou.rayyuan.ebooksearchtaiwan.model.entity.Book
import liou.rayyuan.ebooksearchtaiwan.utils.FragmentArgumentsDelegate

class SimpleWebViewFragment: BaseFragment(), View.OnClickListener, Toolbar.OnMenuItemClickListener {
    companion object {
        const val TAG = "SimpleWebViewFragment"
        fun newInstance(book: Book, showCloseButton: Boolean): SimpleWebViewFragment {
            return SimpleWebViewFragment().apply {
                this.book = book
                this.showCloseButton = showCloseButton
            }
        }
    }

    private val KEY_BOOK = "key-book"
    private val KEY_SHOW_CLOSE_BUTTON = "key-show-close-button"

    private var book by FragmentArgumentsDelegate<Book>()
    private var showCloseButton by FragmentArgumentsDelegate<Boolean>()
    private val bookHistory = mutableListOf<Book>()
    private val customWebViewClient = CustomWebViewClient()

    var onSimpleWebviewActionListener: OnSimpleWebviewActionListener? = null
    private var shareActionProvider: ShareActionProvider? = null
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            book = savedInstanceState.getParcelable(KEY_BOOK) as Book
            showCloseButton = savedInstanceState.getBoolean(KEY_SHOW_CLOSE_BUTTON, false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_simple_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retrieveView(view)

        simple_webview_toolbar.inflateMenu(R.menu.webview_page)
        val menuItem = simple_webview_toolbar.menu.findItem(R.id.webview_page_menu_action_share)
        //FIXME:: ShareActionProvider is null and not work on Release build
        shareActionProvider = MenuItemCompat.getActionProvider(menuItem) as? ShareActionProvider

        simple_webview_toolbar.setOnMenuItemClickListener(this)

        if (showCloseButton) {
            simple_webview_close.visibility = View.VISIBLE
            simple_webview_close.setOnClickListener(this)
        } else {
            simple_webview_close.visibility = View.GONE
        }

        bookHistory.add(book)
        setBookInfo(book)
        initWebView()
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState)
        } else {
            webView.loadUrl(book.link)
        }
    }

    override fun onDestroy() {
        onSimpleWebviewActionListener = null
        super.onDestroy()
    }

    private fun initWebView() {
        with(webView.settings) {
            javaScriptEnabled = true
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (isAdded) {
                    simple_webview_progress_bar.progress = newProgress
                }
            }
        }
        webView.webViewClient = customWebViewClient
    }

    private fun setBookInfo(book: Book) {
        simple_webview_title.text = book.title
        val bookView = BookView(book)
        val authorText = bookView.getAuthors(context!!)
        if (TextUtils.isEmpty(authorText)) {
            simple_webview_description.visibility = View.GONE
        } else {
            simple_webview_description.text = authorText
        }
    }

    fun loadBookResult(book: Book) {
        if (isAdded) {
            this.book = book
            bookHistory.add(book)
            setBookInfo(book)
            customWebViewClient.cleanHistory()
            webView.loadUrl(book.link)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
        outState.putParcelable(KEY_BOOK, book)
        outState.putBoolean(KEY_SHOW_CLOSE_BUTTON, showCloseButton)
    }

    override fun onDestroyView() {
        webView.webChromeClient = null
        webView.webViewClient = null
        super.onDestroyView()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.webview_page_menu_action_open_browser -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(book.link)
                startActivity(intent)
                true
            }
            R.id.webview_page_menu_action_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, book.title)
                intent.putExtra(Intent.EXTRA_TEXT, book.link)
                shareActionProvider?.setShareIntent(intent) ?: run {
                    startActivity(Intent.createChooser(intent, getString(R.string.menu_share_menu_appear)))
                }
                true
            }
            else -> false
        }
    }

    private fun retrieveView(view: View) {
        webView = view.findViewById(R.id.simple_webview_content)
    }

    fun loadUrl(url: String) {
        if (this::webView.isInitialized) {
            webView.loadUrl(url)
        }
    }

    fun goBack(): Boolean {
        if (this::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()

            Log.i("SimpleWebViewFragment", "Book History Size = ${bookHistory.size}")
            if (bookHistory.size > 1) {
                bookHistory.removeAt(bookHistory.lastIndex)
                setBookInfo(bookHistory.last())
            }
            return true
        }
        return false
    }

    //region View.OnClickListener
    override fun onClick(view: View) {
        if (view.id == R.id.simple_webview_close) {
            onSimpleWebviewActionListener?.onSimpleWebViewClose(tag ?: javaClass.simpleName)
        }
    }
    //endregion

    inner class CustomWebViewClient: WebViewClient() {
        private var cleanHistory = false

        internal fun cleanHistory() {
            cleanHistory = true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            if (isAdded) {
                simple_webview_progress_bar.visibility = View.VISIBLE
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            if (isAdded) {
                if (cleanHistory) {
                    cleanHistory = false
                    simple_webview_content.clearHistory()
                    bookHistory.clear()
                }
                simple_webview_progress_bar.visibility = View.GONE
            }
        }
    }

    interface OnSimpleWebviewActionListener {
        fun onSimpleWebViewClose(tag: String)
    }
}