package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.kevinnzou.web.rememberWebViewNavigator
import com.rayliu.commonmain.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.BookUiModel
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.asUiModel
import liou.rayyuan.ebooksearchtaiwan.simplewebview.SimpleWebViewScreen
import liou.rayyuan.ebooksearchtaiwan.ui.theme.LocalDeviceInfo
import liou.rayyuan.ebooksearchtaiwan.ui.theme.pale_slate

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun BookSearchScreen(
    bookSearchViewModel: BookSearchViewModel,
    modifier: Modifier = Modifier,
    onBookSearchItemClick: (book: Book, paneNavigator: ThreePaneScaffoldNavigator<Book>, isTabletSize: Boolean) -> Unit = { _, _, _ -> },
    showAppBarCameraButton: Boolean = false,
    onAppBarCameraButtonPress: () -> Unit = {},
    onMenuSettingClick: () -> Unit = {},
    onShareOptionClick: (book: BookUiModel) -> Unit = {},
    onOpenInBrowserClick: (book: BookUiModel) -> Unit = {},
    checkShouldAskUserRankApp: () -> Unit = {}
) {
    val viewState by bookSearchViewModel.viewState.collectAsStateWithLifecycle()
    val searchKeywords by bookSearchViewModel.searchKeywords.collectAsStateWithLifecycle()
    val focusAction by bookSearchViewModel.focusTextInput.collectAsStateWithLifecycle()
    val virtualKeyboardAction by bookSearchViewModel.showVirtualKeyboard.collectAsStateWithLifecycle()
    val bookStoreDetails by bookSearchViewModel.bookStoreDetails.collectAsStateWithLifecycle()
    val bookSearchResult by bookSearchViewModel.bookSearchResult.collectAsStateWithLifecycle()
    val showSearchRecords by bookSearchViewModel.isShowSearchRecord.collectAsStateWithLifecycle()
    val searchRecords = bookSearchViewModel.searchRecords.collectAsLazyPagingItems()
    val appVersion by bookSearchViewModel.appVersion.collectAsStateWithLifecycle()

    var isTextInputFocused by remember { mutableStateOf(false) }
    var enableCameraButtonClick by remember { mutableStateOf(false) }
    var enableSearchButtonClick by remember { mutableStateOf(false) }
    var showCopyUrlOption by remember { mutableStateOf(false) }
    var showShareSnapshotOption by remember { mutableStateOf(false) }

    val paneNavigator: ThreePaneScaffoldNavigator<Book> =
        rememberListDetailPaneScaffoldNavigator<Book>()
    BackHandler(enabled = (paneNavigator.canNavigateBack() || isTextInputFocused)) {
        if (isTextInputFocused) {
            bookSearchViewModel.forceFocusOrUnfocusKeywordTextInput(false)
            return@BackHandler
        }

        paneNavigator.navigateBack()
    }
    val isDetailPaneVisible = paneNavigator.scaffoldValue.secondary == PaneAdaptedValue.Expanded
    val isTabletSize = LocalDeviceInfo.current.isTabletSize

    ListDetailPaneScaffold(
        directive = paneNavigator.scaffoldDirective,
        value = paneNavigator.scaffoldValue,
        modifier = modifier,
        listPane = {
            AnimatedPane {
                BookResultListScreen(
                    viewState = viewState,
                    appVersion = appVersion,
                    searchKeywords = searchKeywords,
                    bookStoreDetails = bookStoreDetails,
                    bookSearchResult = bookSearchResult,
                    showSearchRecords = showSearchRecords,
                    searchRecords = searchRecords,
                    onBookSearchItemClick = { onBookSearchItemClick(it, paneNavigator, isTabletSize) },
                    showAppBarCameraButton = showAppBarCameraButton,
                    onAppBarCameraButtonPress = onAppBarCameraButtonPress,
                    onMenuSettingClick = onMenuSettingClick,
                    focusAction = focusAction,
                    virtualKeyboardAction = virtualKeyboardAction,
                    enableCameraButtonClick = enableCameraButtonClick,
                    enableSearchButtonClick = enableSearchButtonClick,
                    showCopyUrlOption = showCopyUrlOption,
                    showShareSnapshotOption = showShareSnapshotOption,
                    lastScrollPosition = bookSearchViewModel.lastScrollPosition,
                    lastScrollOffset = bookSearchViewModel.lastScrollOffset,
                    onSearchRecordClick = { searchRecord ->
                        val keyword = searchRecord.text
                        bookSearchViewModel.updateKeyword(TextFieldValue(keyword, selection = TextRange(keyword.length)))
                        bookSearchViewModel.forceFocusOrUnfocusKeywordTextInput(false)
                        bookSearchViewModel.forceShowOrHideVirtualKeyboard(false)
                        bookSearchViewModel.searchBook(keyword)
                    },
                    onDeleteSearchRecord = {
                        bookSearchViewModel.deleteRecords(it)
                    },
                    onDismissSearchRecordCover = {
                        bookSearchViewModel.showSearchRecords(false)
                        bookSearchViewModel.forceShowOrHideVirtualKeyboard(false)
                    },
                    onSearchKeywordTextChange = {
                        bookSearchViewModel.updateKeyword(it)
                    },
                    onPressSearch = {
                        bookSearchViewModel.searchBook()
                    },
                    focusOnSearchBox = {
                        bookSearchViewModel.forceFocusOrUnfocusKeywordTextInput(true)
                        bookSearchViewModel.forceShowOrHideVirtualKeyboard(true)
                    },
                    onFocusActionFinish = {
                        bookSearchViewModel.resetFocusAction()
                    },
                    onKeyboardActionFinish = {
                        bookSearchViewModel.resetVirtualKeyboardState()
                    },
                    onFocusChange = {
                        isTextInputFocused = it.isFocused
                        bookSearchViewModel.focusOnEditText(it.isFocused)
                    },
                    onSearchButtonPress = {
                        bookSearchViewModel.forceShowOrHideVirtualKeyboard(false)
                        bookSearchViewModel.forceFocusOrUnfocusKeywordTextInput(false)
                        bookSearchViewModel.searchBook()
                    },
                    onClickCopySnapshotToClipboard = {
                        bookSearchViewModel.copySnapshotToClipboard()
                    },
                    onClickShareSnapshot = {
                        bookSearchViewModel.shareCurrentSnapshot()
                    },
                    onBookResultListScroll = {
                        if (isTextInputFocused) {
                            bookSearchViewModel.forceFocusOrUnfocusKeywordTextInput(false)
                        }
                    },
                    onSaveBookResultListPreviousScrollPosition = { position, offset ->
                        bookSearchViewModel.savePreviousScrollPosition(position, offset)
                    },
                    onPrepareBookResult = {
                        enableCameraButtonClick = false
                        enableSearchButtonClick = false
                        showCopyUrlOption = false
                        showShareSnapshotOption = false
                    },
                    onShowBooksResult = {
                        enableCameraButtonClick = true
                        enableSearchButtonClick = true
                        showCopyUrlOption = true
                        showShareSnapshotOption = true
                    },
                    onShowServiceList = {
                        enableCameraButtonClick = true
                        enableSearchButtonClick = true
                        showCopyUrlOption = false
                        showShareSnapshotOption = false
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        },
        detailPane = {
            AnimatedPane {
                val book = paneNavigator.currentDestination?.content?.asUiModel()
                if (book != null) {
                    val webViewNavigator = rememberWebViewNavigator()
                    SimpleWebViewScreen(
                        book = book,
                        webViewNavigator = webViewNavigator,
                        onBackButtonPress = {
                            if (paneNavigator.canNavigateBack()) {
                                paneNavigator.navigateBack()
                            }
                        },
                        showCloseButton = !isDetailPaneVisible,
                        onShareOptionClick = onShareOptionClick,
                        onOpenInBrowserClick = onOpenInBrowserClick
                    )

                    // FIXME: Logic is not same as the original one
                    if (!webViewNavigator.canGoBack) {
                        checkShouldAskUserRankApp()
                    }
                } else {
                    Scaffold(
                        contentWindowInsets = WindowInsets.safeDrawing
                    ) { paddings ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier =
                                Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(pale_slate)
                                    .fillMaxSize()
                                    .consumeWindowInsets(paddings)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.big_icon),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier =
                                    Modifier
                                        .size(200.dp)
                                        .alpha(0.3f)
                            )
                        }
                    }
                }
            }
        }
    )
}
