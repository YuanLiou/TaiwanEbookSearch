package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    focusOnSearchBox: () -> Unit = {}
) {
    composable(
        route = BookResultDestinations.ServiceStatus.route,
    ) {
        ServiceListScreen(
            viewModel = viewModel,
            modifier = modifier
        )
    }
    composable(
        route = BookResultDestinations.SearchResult.route,
    ) {
        BookSearchResultScreen(
            viewModel = viewModel,
            modifier = modifier,
            onBookSearchItemClick = onBookSearchItemClick,
            focusOnSearchBox = focusOnSearchBox
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
