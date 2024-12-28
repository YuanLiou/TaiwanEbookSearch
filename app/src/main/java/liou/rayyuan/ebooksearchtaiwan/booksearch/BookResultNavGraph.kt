package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rayliu.commonmain.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.booksearch.screen.BookSearchResultScreen
import liou.rayyuan.ebooksearchtaiwan.booksearch.screen.ServiceListScreen
import liou.rayyuan.ebooksearchtaiwan.navigation.BookResultDestinations
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

fun NavGraphBuilder.bookResultNavGraph(
    viewModel: BookSearchViewModel,
    modifier: Modifier = Modifier,
    onBookSearchItemClick: (Book) -> Unit = {},
    focusOnSearchBox: () -> Unit = {},
    onListScroll: () -> Unit = {}
) {
    composable(
        route = BookResultDestinations.ServiceStatus.route,
    ) {
        val bookStoreDetails =
            viewModel.bookStoreDetails
                .collectAsStateWithLifecycle()
                .value
        ServiceListScreen(
            bookStoreDetails = bookStoreDetails,
            modifier = modifier,
        )
    }
    composable(
        route = BookResultDestinations.SearchResult.route,
    ) {
        val bookSearchResult =
            viewModel.bookSearchResult
                .collectAsStateWithLifecycle()
                .value

        BookSearchResultScreen(
            viewModel = viewModel,
            bookSearchResult = bookSearchResult,
            lastScrollPosition = viewModel.lastScrollPosition,
            lastScrollOffset = viewModel.lastScrollOffset,
            modifier = modifier,
            onBookSearchItemClick = onBookSearchItemClick,
            focusOnSearchBox = focusOnSearchBox,
            onListScroll = onListScroll
        )
    }
    composable(
        route = BookResultDestinations.LoadingScreen.route,
    ) { _ ->
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = EBookTheme.colors.colorPrimary
            )
        }
    }
}
