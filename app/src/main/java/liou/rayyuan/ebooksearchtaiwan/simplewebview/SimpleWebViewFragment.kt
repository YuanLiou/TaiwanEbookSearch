package liou.rayyuan.ebooksearchtaiwan.simplewebview

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.ShareActionProvider
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import liou.rayyuan.ebooksearchtaiwan.BaseFragment
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.BookView
import liou.rayyuan.ebooksearchtaiwan.databinding.FragmentSimpleWebviewBinding
import com.rayliu.commonmain.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.utils.FragmentArgumentsDelegate
import liou.rayyuan.ebooksearchtaiwan.utils.FragmentViewBinding

class SimpleWebViewFragment: BaseFragment(R.layout.fragment_simple_webview), View.OnClickListener, Toolbar.OnMenuItemClickListener {
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
    private val viewBinding: FragmentSimpleWebviewBinding by FragmentViewBinding(FragmentSimpleWebviewBinding::bind)
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
            book = savedInstanceState.getParcelable(KEY_BOOK)!!
            showCloseButton = savedInstanceState.getBoolean(KEY_SHOW_CLOSE_BUTTON, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retrieveView(view)

        val toolbar = viewBinding.simpleWebviewToolbar
        toolbar.inflateMenu(R.menu.webview_page)
        val menuItem = toolbar.menu.findItem(R.id.webview_page_menu_action_share)
        //FIXME:: ShareActionProvider is null and not work on Release build
        shareActionProvider = MenuItemCompat.getActionProvider(menuItem) as? ShareActionProvider

        toolbar.setOnMenuItemClickListener(this)

        val closeView = viewBinding.simpleWebviewClose
        if (showCloseButton) {
            closeView.visibility = View.VISIBLE
            closeView.setOnClickListener(this)
        } else {
            closeView.visibility = View.GONE
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
                    val progressBarView = viewBinding.simpleWebviewProgressBar
                    progressBarView.progress = newProgress
                }
            }
        }
        webView.webViewClient = customWebViewClient
    }

    private fun setBookInfo(book: Book) {
        val titleView = viewBinding.simpleWebviewTitle
        titleView.text = book.title
        val bookView = BookView(book)
        val authorText = bookView.getAuthors(requireContext())
        val descriptionView = viewBinding.simpleWebviewDescription
        if (TextUtils.isEmpty(authorText)) {
            descriptionView.visibility = View.GONE
        } else {
            descriptionView.text = authorText
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
                val progressBarView = viewBinding.simpleWebviewProgressBar
                progressBarView.visibility = View.VISIBLE
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            if (isAdded) {
                val progressBarView = viewBinding.simpleWebviewProgressBar
                val contentView = viewBinding.simpleWebviewContent
                if (cleanHistory) {
                    cleanHistory = false
                    contentView.clearHistory()
                    bookHistory.clear()
                }
                progressBarView.visibility = View.GONE
            }
        }
    }

    interface OnSimpleWebviewActionListener {
        fun onSimpleWebViewClose(tag: String)
    }
}