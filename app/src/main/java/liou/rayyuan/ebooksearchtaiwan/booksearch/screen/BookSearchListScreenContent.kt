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
    lastScrollPosition: Int = 0,
    lastScrollOffset: Int = 0,
    onBookSearchItemClick: (Book) -> Unit = {},
    focusOnSearchBox: () -> Unit = {},
    onListScroll: () -> Unit = {},
    onSavePreviousScrollPosition: (position: Int, offset: Int) -> Unit = { _, _ -> },
    onPrepareBookResult: () -> Unit = {},
    onShowBooksResult: () -> Unit = {},
    onShowServiceList: () -> Unit = {}
) {
    Box(
        modifier = modifier
    ) {
        when (viewState) {
            BookResultViewState.PrepareBookResult -> {
                onPrepareBookResult()
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = EBookTheme.colors.colorPrimary
                    )
                }
            }

            BookResultViewState.ShowBooks -> {
                onShowBooksResult()
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
                onShowServiceList()
                ServiceListScreen(
                    bookStoreDetails = bookStoreDetails,
                    contentPaddings = contentPaddings,
                )
            }
        }
    }
}
