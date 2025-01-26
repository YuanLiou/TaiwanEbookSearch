package liou.rayyuan.ebooksearchtaiwan.simplewebview

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.kevinnzou.web.LoadingState
import com.kevinnzou.web.WebView
import com.kevinnzou.web.WebViewNavigator
import com.kevinnzou.web.rememberSaveableWebViewState
import com.kevinnzou.web.rememberWebViewNavigator
import kotlinx.coroutines.flow.distinctUntilChanged
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.BookUiModel
import liou.rayyuan.ebooksearchtaiwan.composable.EBookDropdownMenu
import liou.rayyuan.ebooksearchtaiwan.composable.OptionMenuItem
import liou.rayyuan.ebooksearchtaiwan.composable.iconpack.BaselineClear24Px
import liou.rayyuan.ebooksearchtaiwan.composable.iconpack.EBookIcons
import liou.rayyuan.ebooksearchtaiwan.composable.resolveColorAttribute
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleWebViewScreen(
    book: BookUiModel,
    modifier: Modifier = Modifier,
    navigator: WebViewNavigator = rememberWebViewNavigator(),
    showCloseButton: Boolean = false,
    onBackButtonPress: () -> Unit = {},
    onShareOptionClick: (book: BookUiModel) -> Unit = {},
    onOpenInBrowserClick: (book: BookUiModel) -> Unit = {},
    onCanWebViewGoBackUpdate: (canGoBack: Boolean) -> Unit = {}
) {
    var showOptionMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        val title = book?.getTitle().orEmpty()
                        Text(
                            text = title,
                            style =
                                TextStyle.Default.copy(
                                    fontSize = 16.sp,
                                    color =
                                        resolveColorAttribute(
                                            LocalContext.current,
                                            android.R.attr.textColorPrimary,
                                            EBookTheme.colors.subtitle1TextColor
                                        ),
                                ),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        val authorText = book?.getAuthors(LocalContext.current)
                        if (!authorText.isNullOrEmpty()) {
                            Text(
                                text = authorText,
                                style =
                                    TextStyle.Default.copy(
                                        fontSize = 12.sp,
                                        color =
                                            resolveColorAttribute(
                                                LocalContext.current,
                                                android.R.attr.textColorPrimary,
                                                EBookTheme.colors.subtitle1TextColor
                                            ),
                                    ),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors().copy(
                        containerColor = EBookTheme.colors.colorBackground,
                        scrolledContainerColor = EBookTheme.colors.colorBackground
                    ),
                navigationIcon = {
                    if (showCloseButton) {
                        IconButton(
                            onClick = onBackButtonPress
                        ) {
                            Icon(
                                imageVector = EBookIcons.BaselineClear24Px,
                                contentDescription = "back button",
                                tint = EBookTheme.colors.colorOnPrimary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            showOptionMenu = !showOptionMenu
                        },
                        colors =
                            IconButtonDefaults.iconButtonColors().copy(
                                contentColor = EBookTheme.colors.colorOnPrimary
                            )
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Option Menu")
                    }

                    EBookDropdownMenu(
                        showOptionMenu = showOptionMenu,
                        onDismissRequest = {
                            showOptionMenu = false
                        }
                    ) {
                        OptionMenuItem(
                            title = stringResource(R.string.menu_share),
                            onClick = {
                                showOptionMenu = false
                                onShareOptionClick(book)
                            }
                        )

                        OptionMenuItem(
                            title = stringResource(R.string.menu_open_in_browser),
                            onClick = {
                                showOptionMenu = false
                                onOpenInBrowserClick(book)
                            }
                        )
                    }
                }
            )
        },
        containerColor = EBookTheme.colors.colorBackground,
        modifier = modifier
    ) { paddings ->
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddings)
        ) {
            val webViewState = rememberSaveableWebViewState()
            LaunchedEffect(Unit) {
                val bundle = webViewState.viewState
                if (bundle == null) {
                    navigator.loadUrl(book.getLink())
                }

                snapshotFlow { navigator.canGoBack }
                    .distinctUntilChanged()
                    .collect {
                        onCanWebViewGoBackUpdate(it)
                    }
            }

            WebView(
                state = webViewState,
                navigator = navigator,
                modifier = Modifier.fillMaxSize(),
                onCreated = { webView ->
                    webView.settings.javaScriptEnabled = true
                }
            )

            val loadingState = webViewState.loadingState
            AnimatedVisibility(
                visible = loadingState is LoadingState.Loading,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopStart),
            ) {
                LinearProgressIndicator(
                    progress = {
                        (loadingState as? LoadingState.Loading)?.progress ?: 0f
                    },
                    color = EBookTheme.colors.colorPrimary,
                    trackColor = EBookTheme.colors.colorPrimaryDark,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                )
            }
        }
    }
}
