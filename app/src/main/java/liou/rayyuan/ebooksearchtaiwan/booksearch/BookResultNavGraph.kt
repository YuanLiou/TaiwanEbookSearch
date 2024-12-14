package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rayliu.commonmain.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.BookSearchList
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.ServiceStatusList
import liou.rayyuan.ebooksearchtaiwan.navigation.BookResultDestinations
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

fun NavGraphBuilder.bookResultNavGraph(
    viewModel: BookSearchViewModel,
    modifier: Modifier = Modifier,
    onBookSearchItemClick: (Book) -> Unit = {}
) {
    composable(
        route = BookResultDestinations.ServiceStatus.route,
    ) { _ ->
        val bookStoreDetails =
            viewModel.bookStoreDetails
                .collectAsStateWithLifecycle()
                .value

        ServiceStatusList(
            storeDetails = bookStoreDetails,
            modifier = modifier
        )
    }
    composable(
        route = BookResultDestinations.SearchResult.route,
    ) { _ ->
        val bookSearchResult =
            viewModel.bookSearchResult
                .collectAsStateWithLifecycle()
                .value

        val lazyListState =
            rememberLazyListState(
                initialFirstVisibleItemIndex = viewModel.lastScrollPosition,
                initialFirstVisibleItemScrollOffset = viewModel.lastScrollOffset
            )

        BookSearchList(
            bookSearchResults = bookSearchResult,
            lazyListState = lazyListState,
            modifier =
                Modifier
                    .padding(horizontal = dimensionResource(R.dimen.search_list_padding_horizontal))
                    .padding(bottom = 80.dp)
                    .then(modifier),
            onBookSearchItemClick = onBookSearchItemClick
        )

        DisposableEffect(Unit) {
            onDispose {
                viewModel.savePreviousScrollPosition(
                    lazyListState.firstVisibleItemIndex,
                    lazyListState.firstVisibleItemScrollOffset
                )
            }
        }
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
