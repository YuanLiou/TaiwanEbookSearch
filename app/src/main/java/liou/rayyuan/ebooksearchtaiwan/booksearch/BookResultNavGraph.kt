package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rayliu.commonmain.domain.model.Book
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.BookSearchList
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.ServiceStatusList
import liou.rayyuan.ebooksearchtaiwan.composable.iconpack.EBookIcons
import liou.rayyuan.ebooksearchtaiwan.composable.iconpack.KeyboardArrowUp24Dp
import liou.rayyuan.ebooksearchtaiwan.navigation.BookResultDestinations
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.ui.theme.blue_green_a50
import liou.rayyuan.ebooksearchtaiwan.ui.theme.blue_green_you

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
        val scope = rememberCoroutineScope()

        val bookSearchResult =
            viewModel.bookSearchResult
                .collectAsStateWithLifecycle()
                .value

        val lazyListState =
            rememberLazyListState(
                initialFirstVisibleItemIndex = viewModel.lastScrollPosition,
                initialFirstVisibleItemScrollOffset = viewModel.lastScrollOffset
            )

        Box(
            modifier = modifier
        ) {
            BookSearchList(
                bookSearchResults = bookSearchResult,
                lazyListState = lazyListState,
                modifier =
                    Modifier
                        .padding(horizontal = dimensionResource(R.dimen.search_list_padding_horizontal))
                        .padding(bottom = 80.dp),
                onBookSearchItemClick = onBookSearchItemClick
            )

            // Scroll to top button
            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 140.dp)
                        .background(blue_green_you, CircleShape)
                        .border(1.dp, blue_green_a50, CircleShape)
                        .clip(CircleShape)
                        .clickable {
                            scope.launch {
                                lazyListState.animateScrollToItem(0)
                            }
                        }
            ) {
                Image(
                    imageVector = EBookIcons.KeyboardArrowUp24Dp,
                    contentDescription = "bottom button",
                    colorFilter = ColorFilter.tint(EBookTheme.colors.editTextInputColor),
                    modifier =
                        Modifier.padding(6.dp)
                )
            }
        }

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
