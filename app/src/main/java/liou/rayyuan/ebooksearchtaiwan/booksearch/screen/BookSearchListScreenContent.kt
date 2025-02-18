package liou.rayyuan.ebooksearchtaiwan.booksearch.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rayliu.commonmain.domain.model.Book
import com.rayliu.commonmain.domain.model.BookStoreDetails
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.BookSearchResultItem
import liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate.BookResultViewState
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@Composable
fun BookSearchListScreenContent(
    viewState: BookResultViewState?,
    modifier: Modifier = Modifier,
    bookStoreDetails: ImmutableList<BookStoreDetails> = persistentListOf(),
    bookSearchResult: ImmutableList<BookSearchResultItem> = persistentListOf(),
    contentPaddings: PaddingValues = PaddingValues(),
    onBookSearchItemClick: (Book) -> Unit = {},
    focusOnSearchBox: () -> Unit = {},
    onListScroll: () -> Unit = {},
    onSavePreviousScrollPosition: (position: Int, offset: Int) -> Unit = { _, _ -> },
    lastScrollPosition: Int = 0,
    lastScrollOffset: Int = 0,
) {
    Box(
        modifier = modifier
    ) {
        when (viewState) {
            BookResultViewState.PrepareBookResult -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = EBookTheme.colors.colorPrimary
                    )
                }
            }

            is BookResultViewState.ShowBooks -> {
                BookSearchResultScreen(
                    bookSearchResult = bookSearchResult,
                    lastScrollPosition = lastScrollPosition,
                    lastScrollOffset = lastScrollOffset,
                    contentPaddings = contentPaddings,
                    onBookSearchItemClick = onBookSearchItemClick,
                    focusOnSearchBox = focusOnSearchBox,
                    onListScroll = onListScroll,
                    onSavePreviousScrollPosition = onSavePreviousScrollPosition
                )
            }

            else -> {
                ServiceListScreen(
                    bookStoreDetails = bookStoreDetails,
                    contentPaddings = contentPaddings,
                )
            }
        }
    }
}
