package liou.rayyuan.ebooksearchtaiwan.booksearch.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rayliu.commonmain.domain.model.Book
import kotlinx.collections.immutable.ImmutableList
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.BookSearchResultItem
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.BookHeader
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.BookUiModel

@Composable
fun BookSearchList(
    bookSearchResults: ImmutableList<BookSearchResultItem>,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    onBookSearchItemClick: (Book) -> Unit = {}
) {
    LazyColumn(
        state = lazyListState,
        modifier = modifier
    ) {
        item {
            AdBanner(modifier = Modifier.fillMaxWidth())
        }
        items(bookSearchResults) { item ->
            when (item) {
                is BookHeader -> {
                    val subTitle = stringResource(item.stringId)
                    val siteInfo = item.siteInfo
                    var statusText = stringResource(R.string.result_nothing)
                    var showResultStatus =
                        if (siteInfo == null) {
                            item.isEmptyResult
                        } else {
                            false
                        }

                    val isSiteOnline = siteInfo?.isOnline
                    val isResultOkay = siteInfo?.isResultOkay
                    val searchResultMessage = siteInfo?.status
                    val isResultEmpty = item.isEmptyResult
                    if (!isResultEmpty && isSiteOnline == true && isResultOkay == true) {
                        showResultStatus = false
                    }

                    if (isSiteOnline == false) {
                        statusText = stringResource(R.string.error_site_is_not_online)
                        showResultStatus = true
                    } else if (isResultOkay == false) {
                        statusText = stringResource(R.string.error_result_is_failed) + "\n" + searchResultMessage
                        showResultStatus = true
                    } else if (isResultEmpty) {
                        statusText = stringResource(R.string.result_nothing)
                        showResultStatus = true
                    }

                    BookHeader(
                        subtitle = subTitle,
                        modifier = Modifier.padding(top = 24.dp),
                        showStatusText = showResultStatus,
                        statusText = statusText
                    )
                }

                is BookUiModel -> {
                    BookSearchItem(
                        uiModel = item,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        onBookSearchItemClick = onBookSearchItemClick
                    )
                }
            }
        }
    }
}
