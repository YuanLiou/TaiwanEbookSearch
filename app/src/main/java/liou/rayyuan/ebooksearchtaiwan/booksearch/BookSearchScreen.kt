package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.kevinnzou.web.rememberWebViewNavigator
import com.rayliu.commonmain.domain.model.Book
import kotlinx.coroutines.flow.distinctUntilChanged
import liou.rayyuan.ebooksearchtaiwan.BookResultDestinations
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.BookUiModel
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.asUiModel
import liou.rayyuan.ebooksearchtaiwan.rememberEBookAppState
import liou.rayyuan.ebooksearchtaiwan.simplewebview.SimpleWebViewScreen

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
    val appState = rememberEBookAppState()
    LaunchedEffect(Unit) {
        bookSearchViewModel.navigationEvents.distinctUntilChanged().collect { destinations ->
            when (destinations) {
                BookResultDestinations.LoadingScreen -> {
                    appState.navigateToLoadingScreen()
                }

                BookResultDestinations.SearchResult -> {
                    appState.navigateToSearchResult()
                }

                BookResultDestinations.ServiceStatus -> {
                    appState.navigateToServiceStatus()
                }
            }
        }
    }

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
                    viewModel = bookSearchViewModel,
                    navHostController = appState.navController,
                    modifier = Modifier.fillMaxSize(),
                    onBookSearchItemClick = { onBookSearchItemClick(it, paneNavigator) },
                    showAppBarCameraButton = showAppBarCameraButton,
                    onAppBarCameraButtonPress = onAppBarCameraButtonPress,
                    onMenuSettingClick = onMenuSettingClick
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
                }
            }
        }
    )
}
