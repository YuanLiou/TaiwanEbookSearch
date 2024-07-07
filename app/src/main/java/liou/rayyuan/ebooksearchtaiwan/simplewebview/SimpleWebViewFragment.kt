package liou.rayyuan.ebooksearchtaiwan.simplewebview

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.os.BundleCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.MaterialToolbar
import liou.rayyuan.ebooksearchtaiwan.BaseFragment
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.databinding.FragmentSimpleWebviewBinding
import com.rayliu.commonmain.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.uimodel.BookUiModel
import liou.rayyuan.ebooksearchtaiwan.uimodel.asUiModel
import liou.rayyuan.ebooksearchtaiwan.utils.FragmentArgumentsDelegate
import liou.rayyuan.ebooksearchtaiwan.utils.FragmentViewBinding
import liou.rayyuan.ebooksearchtaiwan.utils.setupEdgeToEdge

class SimpleWebViewFragment :
    BaseFragment(R.layout.fragment_simple_webview),
    Toolbar.OnMenuItemClickListener {
    companion object {
        const val TAG = "SimpleWebViewFragment"
        private const val KEY_BOOK = "key-book"
        private const val KEY_SHOW_CLOSE_BUTTON = "key-show-close-button"

        fun newInstance(
            book: Book,
            showCloseButton: Boolean
        ): SimpleWebViewFragment =
            SimpleWebViewFragment().apply {
                this.book = book
                this.showCloseButton = showCloseButton
            }
    }

    private val viewBinding: FragmentSimpleWebviewBinding by FragmentViewBinding(FragmentSimpleWebviewBinding::bind)
    private var book by FragmentArgumentsDelegate<Book>()
    private var showCloseButton by FragmentArgumentsDelegate<Boolean>()
    private val customWebViewClient = CustomWebViewClient()

    var onSimpleWebViewActionListener: OnSimpleWebViewActionListener? = null
    private lateinit var toolbar: MaterialToolbar
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            val book = BundleCompat.getParcelable(savedInstanceState, KEY_BOOK, Book::class.java)
            if (book != null) {
                this.book = book
            }
            showCloseButton = savedInstanceState.getBoolean(KEY_SHOW_CLOSE_BUTTON, false)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        retrieveView(view)

        val toolbar = viewBinding.simpleWebviewToolbar
        toolbar.inflateMenu(R.menu.webview_page)
        toolbar.setOnMenuItemClickListener(this)
        this.toolbar = toolbar

        if (showCloseButton) {
            toolbar.setNavigationIcon(R.drawable.ic_baseline_clear_24px)
            toolbar.setNavigationOnClickListener(
                object : View.OnClickListener {
                    override fun onClick(view: View) {
                        onCloseButtonClick()
                    }
                }
            )

            if (!isDarkTheme()) {
                toolbar.setNavigationIconTint(ContextCompat.getColor(requireContext(), R.color.darker_gray_3B))
            }
        }
        setBookInfo(book.asUiModel())
        initWebView()
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState)
        } else {
            webView.loadUrl(book.link)
        }
        setupEdgeToEdge()
    }

    override fun onDestroy() {
        onSimpleWebViewActionListener = null
        super.onDestroy()
    }

    private fun setupEdgeToEdge() {
        viewBinding.root.setupEdgeToEdge { view, insets ->
            val bars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout()
                )

            view.updatePadding(
                left = bars.left,
                right = bars.right,
                bottom = bars.bottom
            )

            val layoutParams = viewBinding.simpleWebviewTopSpacing.layoutParams
            layoutParams.height = bars.top
            viewBinding.simpleWebviewTopSpacing.layoutParams = layoutParams
        }
    }

    private fun initWebView() {
        with(webView.settings) {
            javaScriptEnabled = true
        }

        webView.webChromeClient =
            object : WebChromeClient() {
                override fun onProgressChanged(
                    view: WebView,
                    newProgress: Int
                ) {
                    if (isAdded) {
                        val progressBarView = viewBinding.simpleWebviewProgressBar
                        progressBarView.isIndeterminate = false
                        progressBarView.setProgressCompat(newProgress, true)
                    }
                }
            }
        webView.webViewClient = customWebViewClient
    }

    private fun setBookInfo(uiModel: BookUiModel) {
        toolbar.title = uiModel.getTitle()
        val authorText = uiModel.getAuthors(requireContext())
        if (!authorText.isNullOrEmpty()) {
            toolbar.subtitle = authorText
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

    override fun onMenuItemClick(item: MenuItem): Boolean =
        when (item.itemId) {
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

    private fun retrieveView(view: View) {
        webView = view.findViewById(R.id.simple_webview_content)
    }

    fun goBack(): Boolean {
        if (!this::webView.isInitialized) {
            return false
        }

        if (webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return false
    }

    //region View.OnClickListener
    private fun onCloseButtonClick() {
        onSimpleWebViewActionListener?.onSimpleWebViewClose(tag ?: javaClass.simpleName)
    }
    //endregion

    inner class CustomWebViewClient : WebViewClient() {
        override fun onPageStarted(
            view: WebView?,
            url: String?,
            favicon: Bitmap?
        ) {
        }

        override fun onPageFinished(
            view: WebView?,
            url: String?
        ) {
            if (isAdded) {
                val progressBarView = viewBinding.simpleWebviewProgressBar
                progressBarView.visibility = View.GONE
            }
        }
    }

    interface OnSimpleWebViewActionListener {
        fun onSimpleWebViewClose(tag: String)
    }
}
