package liou.rayyuan.ebooksearchtaiwan.booksearch.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.rayliu.commonmain.domain.model.SearchRecord
import liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchViewModel
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.SearchRecordItem
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.SearchRecords
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.ServiceStatusList
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@Composable
fun ServiceListScreen(
    viewModel: BookSearchViewModel,
    modifier: Modifier = Modifier,
    onRecordClick: (record: SearchRecord) -> Unit = {},
    onRemoveRecordClick: (record: SearchRecord) -> Unit = {}
) {
    val bookStoreDetails =
        viewModel.bookStoreDetails
            .collectAsStateWithLifecycle()
            .value

    val searchRecords: LazyPagingItems<SearchRecord> = viewModel.searchRecordLiveData.collectAsLazyPagingItems()
    Box(
        modifier = modifier
    ) {
        SearchRecords(
            modifier = Modifier.zIndex(2f),
        ) {
            LazyColumn {
                items(count = searchRecords.itemCount, key = searchRecords.itemKey { it.id ?: -1 }) { index ->
                    searchRecords[index]?.let { searchRecord ->
                        if (index != 0) {
                            HorizontalDivider(
                                color = EBookTheme.colors.colorControlNormal.copy(alpha = 0.3f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        SearchRecordItem(
                            searchRecord = searchRecord,
                            onRecordClick = onRecordClick,
                            onRemoveRecordClick = onRemoveRecordClick
                        )
                    }
                }
            }
        }

        ServiceStatusList(
            storeDetails = bookStoreDetails,
        )
    }
}
