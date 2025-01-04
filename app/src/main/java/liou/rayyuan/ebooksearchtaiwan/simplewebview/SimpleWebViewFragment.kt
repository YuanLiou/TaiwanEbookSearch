package liou.rayyuan.ebooksearchtaiwan.simplewebview

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.BundleCompat
import liou.rayyuan.ebooksearchtaiwan.BaseFragment
import liou.rayyuan.ebooksearchtaiwan.R
import com.rayliu.commonmain.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.asUiModel
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.utils.FragmentArgumentsDelegate
import org.koin.androidx.viewmodel.ext.android.viewModel

class SimpleWebViewFragment : BaseFragment() {
    private val viewModel: SimpleWebViewViewModel by viewModel()
    private var book by FragmentArgumentsDelegate<Book>()
    private var showCloseButton by FragmentArgumentsDelegate<Boolean>()
//    private val customWebViewClient = CustomWebViewClient()

    var onSimpleWebViewActionListener: OnSimpleWebViewActionListener? = null
//    private lateinit var webView: WebView

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
        setContent {
            EBookTheme(isDarkTheme()) {
                SimpleWebViewScreen(
                    book = book.asUiModel(),
                    showCloseButton = showCloseButton,
                    onBackButtonPress = {
                        popOut()
                    },
                    onShareOptionClick = {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_SUBJECT, book.title)
                        intent.putExtra(Intent.EXTRA_TEXT, "${book.title} \n ${book.link}")
                        startActivity(Intent.createChooser(intent, getString(R.string.menu_share_menu_appear)))
                    },
                    onOpenInBrowserClick = {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(book.link)
                        startActivity(intent)
                    }
                )
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        initWebView()
//        if (savedInstanceState != null) {
//            webView.restoreState(savedInstanceState)
//        } else {
//            webView.loadUrl(book.link)
//        }
    }

    override fun onDestroy() {
        onSimpleWebViewActionListener = null
        super.onDestroy()
    }

    private fun popOut() {
        onSimpleWebViewActionListener?.onSimpleWebViewClose(tag ?: javaClass.simpleName)
    }

    private fun initWebView() {
//        with(webView.settings) {
//            javaScriptEnabled = true
//        }
//
//        webView.webChromeClient =
//            object : WebChromeClient() {
//                override fun onProgressChanged(
//                    view: WebView,
//                    newProgress: Int
//                ) {
//                    if (isAdded) {
//                        val progressBarView = viewBinding.simpleWebviewProgressBar
//                        progressBarView.isIndeterminate = false
//                        progressBarView.setProgressCompat(newProgress, true)
//                    }
//                }
//            }
//        webView.webViewClient = customWebViewClient
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        webView.saveState(outState)
        outState.putParcelable(KEY_BOOK, book)
        outState.putBoolean(KEY_SHOW_CLOSE_BUTTON, showCloseButton)
    }

    override fun onDestroyView() {
//        webView.webChromeClient = null
        super.onDestroyView()
    }

    fun goBack(): Boolean {
//        if (!this::webView.isInitialized) {
//            return false
//        }
//
//        if (webView.canGoBack()) {
//            webView.goBack()
//            return true
//        }
        return false
    }

    inner class CustomWebViewClient : WebViewClient() {
        override fun onPageStarted(
            view: WebView?,
            url: String?,
            favicon: Bitmap?
        ) = Unit

        override fun onPageFinished(
            view: WebView?,
            url: String?
        ) {
//            if (isAdded) {
//                val progressBarView = viewBinding.simpleWebviewProgressBar
//                progressBarView.visibility = View.GONE
//            }
        }
    }

    interface OnSimpleWebViewActionListener {
        fun onSimpleWebViewClose(tag: String)
    }

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
}
