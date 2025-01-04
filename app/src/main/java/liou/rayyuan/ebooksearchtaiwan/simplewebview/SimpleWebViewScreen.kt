package liou.rayyuan.ebooksearchtaiwan.simplewebview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.kevinnzou.web.WebView
import com.kevinnzou.web.rememberWebViewState
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.BookUiModel
import liou.rayyuan.ebooksearchtaiwan.composable.EBookDropdownMenu
import liou.rayyuan.ebooksearchtaiwan.composable.OptionMenuItem
import liou.rayyuan.ebooksearchtaiwan.composable.iconpack.BaselineClear24Px
import liou.rayyuan.ebooksearchtaiwan.composable.iconpack.EBookIcons
import liou.rayyuan.ebooksearchtaiwan.composable.resolveColorAttribute
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleWebViewScreen(
    book: BookUiModel,
    modifier: Modifier = Modifier,
    showCloseButton: Boolean = true,
    onBackButtonPress: () -> Unit = {},
    onShareOptionClick: () -> Unit = {},
    onOpenInBrowserClick: () -> Unit = {}
) {
    var showOptionMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        val title = book.getTitle()
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
                        val authorText = book.getAuthors(LocalContext.current)
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
                                onShareOptionClick()
                            }
                        )

                        OptionMenuItem(
                            title = stringResource(R.string.menu_open_in_browser),
                            onClick = {
                                showOptionMenu = false
                                onOpenInBrowserClick()
                            }
                        )
                    }
                }
            )
        },
        containerColor = EBookTheme.colors.colorBackground,
        modifier = modifier
    ) { paddings ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddings)
        ) {
            val webViewState = rememberWebViewState(book.getLink())
            WebView(
                state = webViewState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
