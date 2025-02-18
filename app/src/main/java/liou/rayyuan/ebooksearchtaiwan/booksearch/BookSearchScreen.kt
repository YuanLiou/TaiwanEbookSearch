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
import liou.rayyuan.ebooksearchtaiwan.ui.theme.pale_slate

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun BookSearchScreen(
    bookSearchViewModel: BookSearchViewModel,
    modifier: Modifier = Modifier,
    onBookSearchItemClick: (book: Book, paneNavigator: ThreePaneScaffoldNavigator<Book>) -> Unit = { _, _ -> },
    showAppBarCameraButton: Boolean = false,
    onAppBarCameraButtonPress: () -> Unit = {},
    onMenuSettingClick: () -> Unit = {},
    onShareOptionClick: (book: BookUiModel) -> Unit = {},
    onOpenInBrowserClick: (book: BookUiModel) -> Unit = {},
    checkShouldAskUserRankApp: () -> Unit = {}
) {
    val viewState = bookSearchViewModel.viewState.collectAsStateWithLifecycle().value

    val searchKeywords =
        bookSearchViewModel.searchKeywords
            .collectAsStateWithLifecycle()
            .value

    val focusAction =
        bookSearchViewModel.focusTextInput
            .collectAsStateWithLifecycle()
            .value

    val virtualKeyboardAction =
        bookSearchViewModel.showVirtualKeyboard
            .collectAsStateWithLifecycle()
            .value

    val enableCameraButtonClick =
        bookSearchViewModel.enableCameraButtonClick
            .collectAsStateWithLifecycle()
            .value

    val enableSearchButtonClick =
        bookSearchViewModel.enableSearchButtonClick
            .collectAsStateWithLifecycle()
            .value

    val isLoadingResult =
        bookSearchViewModel.isLoadingResult
            .collectAsStateWithLifecycle()
            .value

    val isTextInputFocused =
        bookSearchViewModel.isTextInputFocused
            .collectAsStateWithLifecycle()
            .value

    val bookStoreDetails =
        bookSearchViewModel.bookStoreDetails
            .collectAsStateWithLifecycle()
            .value

    val bookSearchResult =
        bookSearchViewModel.bookSearchResult
            .collectAsStateWithLifecycle()
            .value

    val showSearchRecords =
        bookSearchViewModel.isShowSearchRecord
            .collectAsStateWithLifecycle()
            .value
    val searchRecords = bookSearchViewModel.searchRecords.collectAsLazyPagingItems()

    val paneNavigator: ThreePaneScaffoldNavigator<Book> =
        rememberListDetailPaneScaffoldNavigator<Book>()
    BackHandler(paneNavigator.canNavigateBack()) {
        paneNavigator.navigateBack()
    }
    val isDetailPaneVisible = paneNavigator.scaffoldValue.secondary == PaneAdaptedValue.Expanded

    ListDetailPaneScaffold(
        directive = paneNavigator.scaffoldDirective,
        value = paneNavigator.scaffoldValue,
        modifier = modifier,
        listPane = {
            AnimatedPane {
                BookResultListScreen(
                    viewState = viewState,
                    searchKeywords = searchKeywords,
                    bookStoreDetails = bookStoreDetails,
                    bookSearchResult = bookSearchResult,
                    showSearchRecords = showSearchRecords,
                    searchRecords = searchRecords,
                    onBookSearchItemClick = { onBookSearchItemClick(it, paneNavigator) },
                    showAppBarCameraButton = showAppBarCameraButton,
                    onAppBarCameraButtonPress = onAppBarCameraButtonPress,
                    onMenuSettingClick = onMenuSettingClick,
                    focusAction = focusAction,
                    virtualKeyboardAction = virtualKeyboardAction,
                    enableCameraButtonClick = enableCameraButtonClick,
                    enableSearchButtonClick = enableSearchButtonClick,
                    isLoadingResult = isLoadingResult,
                    showCopyUrlOption = bookSearchViewModel.showCopyUrlOption,
                    showShareSnapshotOption = bookSearchViewModel.showShareSnapshotOption,
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
                    onFocusChange = {
                        bookSearchViewModel.updateTextInputFocusState(it.isFocused)
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
