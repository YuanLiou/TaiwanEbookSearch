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
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import com.google.android.material.appbar.MaterialToolbar
import liou.rayyuan.ebooksearchtaiwan.BaseFragment
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.BookView
import liou.rayyuan.ebooksearchtaiwan.databinding.FragmentSimpleWebviewBinding
import com.rayliu.commonmain.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.utils.FragmentArgumentsDelegate
import liou.rayyuan.ebooksearchtaiwan.utils.FragmentViewBinding

class SimpleWebViewFragment: BaseFragment(R.layout.fragment_simple_webview), Toolbar.OnMenuItemClickListener {
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
    private lateinit var toolbar: MaterialToolbar
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
        toolbar.setOnMenuItemClickListener(this)
        this.toolbar = toolbar

        if (showCloseButton) {
            toolbar.setNavigationIcon(R.drawable.ic_baseline_clear_24px)
            toolbar.setNavigationOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    onCloseButtonClick()
                }
            })

            if (!isDarkTheme()) {
                toolbar.setNavigationIconTint(ContextCompat.getColor(requireContext(), R.color.darker_gray_3B))
            }
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
                    progressBarView.isIndeterminate = false
                    progressBarView.setProgressCompat(newProgress, true)
                }
            }
        }
        webView.webViewClient = customWebViewClient
    }

    private fun setBookInfo(book: Book) {
        toolbar.title = book.title
        val bookView = BookView(book)
        val authorText = bookView.getAuthors(requireContext())
        if (!TextUtils.isEmpty(authorText)) {
            toolbar.subtitle = authorText
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
                intent.putExtra(Intent.EXTRA_TEXT, "${book.title} \n ${book.link}")
                startActivity(Intent.createChooser(intent, getString(R.string.menu_share_menu_appear)))
                true
            }
            else -> false
        }
    }

    private fun retrieveView(view: View) {
        webView = view.findViewById(R.id.simple_webview_content)
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
    private fun onCloseButtonClick() {
        onSimpleWebviewActionListener?.onSimpleWebViewClose(tag ?: javaClass.simpleName)
    }
    //endregion

    inner class CustomWebViewClient: WebViewClient() {
        private var cleanHistory = false

        internal fun cleanHistory() {
            cleanHistory = true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
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