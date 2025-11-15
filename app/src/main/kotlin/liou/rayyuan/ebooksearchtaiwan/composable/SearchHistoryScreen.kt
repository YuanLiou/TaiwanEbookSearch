
package liou.rayyuan.ebooksearchtaiwan.composable

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

data class SearchHistoryRecord(
    val title: String,
    val count: Int,
    val lastSearchDate: String
)

@Composable
fun SearchHistoryItem(
    record: SearchHistoryRecord,
    onCopyClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = record.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "上次搜尋日：${record.lastSearchDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                IconButton(onClick = onCopyClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_content_copy_24),
                        contentDescription = "Copy",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHistoryTopAppBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = { Text("我的搜尋紀錄") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = EBookTheme.colors.customTabHeaderColor
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHistoryScreen(
    searchHistoryRecords: List<SearchHistoryRecord>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SearchHistoryTopAppBar(
                onBackClick = { /*TODO*/ }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Do nothing for the FAB itself */ },
                shape = RoundedCornerShape(50),
                containerColor = Color(0xFF4DB6AC) // Teal color from target
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { /* TODO: onDeleteAllClick */ }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete All",
                            tint = Color.White
                        )
                    }
                    Row(
                        modifier = Modifier.clickable { /* TODO: onDownloadClick */ }.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_download_24),
                            contentDescription = "Download",
                            tint = Color.White
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            items(searchHistoryRecords) { record ->
                SearchHistoryItem(
                    record = record,
                    onCopyClick = { /*TODO*/ },
                    onDeleteClick = { /*TODO*/ }
                )
                HorizontalDivider(color = EBookTheme.colors.dividerColor)
            }
        }
    }
}

@Preview(name = "Search History Item Light", showBackground = true)
@Preview(name = "Search History Item Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun SearchHistoryItemPreview() {
    EBookTheme {
        SearchHistoryItem(
            record = SearchHistoryRecord("紀錄 001", 1, "2025/11/01"),
            onCopyClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(name = "Search History Screen Light", showSystemUi = true)
@Preview(name = "Search History Screen Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showSystemUi = true)
@Composable
private fun SearchHistoryScreenPreview() {
    val mockRecords = listOf(
        SearchHistoryRecord("紀錄 001", 1, "2025/11/01"),
        SearchHistoryRecord("紀錄 002", 2, "2025/10/12"),
        SearchHistoryRecord("紀錄 003", 1, "2025/09/01"),
        SearchHistoryRecord("紀錄 004", 3, "2025/10/21"),
        SearchHistoryRecord("紀錄 005", 1, "2025/09/06"),
        SearchHistoryRecord("紀錄 006", 1, "2025/09/07")
    )
    EBookTheme {
        SearchHistoryScreen(searchHistoryRecords = mockRecords)
    }
}
