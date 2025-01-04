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
import androidx.core.os.bundleOf
import liou.rayyuan.ebooksearchtaiwan.BaseFragment
import liou.rayyuan.ebooksearchtaiwan.R
import com.rayliu.commonmain.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class SimpleWebViewFragment : BaseFragment() {
    private val viewModel: SimpleWebViewViewModel by viewModel()
//    private val customWebViewClient = CustomWebViewClient()

    var onSimpleWebViewActionListener: OnSimpleWebViewActionListener? = null
//    private lateinit var webView: WebView

    private var canWebViewGoBack = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
        setContent {
            EBookTheme(isDarkTheme()) {
                SimpleWebViewScreen(
                    viewModel = viewModel,
                    onBackButtonPress = {
                        popOut()
                    },
                    onShareOptionClick = { bookUiModel ->
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_SUBJECT, bookUiModel.getTitle())
                        intent.putExtra(Intent.EXTRA_TEXT, bookUiModel.getShareText())
                        startActivity(Intent.createChooser(intent, getString(R.string.menu_share_menu_appear)))
                    },
                    onOpenInBrowserClick = { bookUiModel ->
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(bookUiModel.getLink())
                        startActivity(intent)
                    },
                    onCanWebViewGoBackUpdate = { canGoBack ->
                        canWebViewGoBack = canGoBack
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
    }

    override fun onDestroy() {
        onSimpleWebViewActionListener = null
        super.onDestroy()
    }

    private fun popOut() {
        onSimpleWebViewActionListener?.onSimpleWebViewClose(tag ?: javaClass.simpleName)
    }

    private fun initWebView() {
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

    override fun onDestroyView() {
//        webView.webChromeClient = null
        super.onDestroyView()
    }

    fun goBack(): Boolean = canWebViewGoBack

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
        internal const val KEY_BOOK = "key-book"
        internal const val KEY_SHOW_CLOSE_BUTTON = "key-show-close-button"

        fun newInstance(
            book: Book,
            showCloseButton: Boolean
        ): SimpleWebViewFragment {
            val bundle =
                bundleOf(
                    KEY_BOOK to book,
                    KEY_SHOW_CLOSE_BUTTON to showCloseButton
                )
            return SimpleWebViewFragment().also {
                it.arguments = bundle
            }
        }
    }
}
