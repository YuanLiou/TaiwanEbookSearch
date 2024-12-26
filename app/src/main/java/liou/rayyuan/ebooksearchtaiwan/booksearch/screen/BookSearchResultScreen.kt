package liou.rayyuan.ebooksearchtaiwan.booksearch.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rayliu.commonmain.domain.model.Book
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchViewModel
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.BookSearchList
import liou.rayyuan.ebooksearchtaiwan.composable.iconpack.EBookIcons
import liou.rayyuan.ebooksearchtaiwan.composable.iconpack.KeyboardArrowUp24Dp
import liou.rayyuan.ebooksearchtaiwan.composable.iconpack.SearchBlack24Dp
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.ui.theme.blue_green_a50
import liou.rayyuan.ebooksearchtaiwan.ui.theme.blue_green_you

@Composable
fun BookSearchResultScreen(
    viewModel: BookSearchViewModel,
    modifier: Modifier = Modifier,
    onBookSearchItemClick: (Book) -> Unit = {},
    focusOnSearchBox: () -> Unit = {}
) {
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
                Modifier.padding(horizontal = dimensionResource(R.dimen.search_list_padding_horizontal)),
            onBookSearchItemClick = onBookSearchItemClick
        )

        // Scroll to top button
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
                    .background(blue_green_you, CircleShape)
                    .border(1.dp, blue_green_a50, CircleShape)
                    .clip(CircleShape)
                    .clickable {
                        if (lazyListState.canScrollBackward) {
                            scope.launch {
                                lazyListState.animateScrollToItem(0)
                            }
                        } else {
                            focusOnSearchBox()
                        }
                    }
        ) {
            val icon =
                if (lazyListState.canScrollBackward) {
                    EBookIcons.KeyboardArrowUp24Dp
                } else {
                    EBookIcons.SearchBlack24Dp
                }

            Image(
                imageVector = icon,
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
