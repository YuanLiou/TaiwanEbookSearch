package liou.rayyuan.ebooksearchtaiwan.booksearch.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.rayliu.commonmain.domain.model.Book
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchViewModel
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.BookSearchList
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.BookSearchResultItem
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
    bookSearchResult: ImmutableList<BookSearchResultItem> = persistentListOf(),
    lastScrollPosition: Int = 0,
    lastScrollOffset: Int = 0,
    onBookSearchItemClick: (Book) -> Unit = {},
    focusOnSearchBox: () -> Unit = {},
    onListScroll: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    val lazyListState =
        rememberLazyListState(
            initialFirstVisibleItemIndex = lastScrollPosition,
            initialFirstVisibleItemScrollOffset = lastScrollOffset
        )

    val maxHeight = 40.dp
    var backToTopButtonOffset by remember { mutableStateOf(maxHeight) }
    var backToTopButtonAlpha by remember { mutableFloatStateOf(1f) }
    val nestedScrollConnection =
        remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    val delta = available.y
                    val newOffset = backToTopButtonOffset + delta.dp
                    backToTopButtonOffset = newOffset.coerceIn((-maxHeight), maxHeight)
                    val newAlpha = (backToTopButtonOffset + maxHeight) / (maxHeight * 2)
                    backToTopButtonAlpha = newAlpha.coerceIn(0f, 1f)
                    onListScroll()
                    return super.onPreScroll(available, source)
                }
            }
        }

    Box(
        modifier = modifier.nestedScroll(nestedScrollConnection)
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
                    .offset { IntOffset(0, (backToTopButtonOffset * -1f).roundToPx()) }
                    .alpha(backToTopButtonAlpha)
                    .background(blue_green_you, CircleShape)
                    .border(1.dp, blue_green_a50, CircleShape)
                    .clip(CircleShape)
                    .clickable {
                        if (backToTopButtonAlpha < 0.5f) {
                            return@clickable
                        }

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
