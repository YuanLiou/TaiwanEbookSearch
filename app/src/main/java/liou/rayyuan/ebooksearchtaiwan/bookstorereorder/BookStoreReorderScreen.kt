package liou.rayyuan.ebooksearchtaiwan.bookstorereorder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.bookstorereorder.composable.BookStoreOrderItem
import liou.rayyuan.ebooksearchtaiwan.bookstorereorder.model.SortedStore
import liou.rayyuan.ebooksearchtaiwan.composable.dragContainer
import liou.rayyuan.ebooksearchtaiwan.composable.draggableItems
import liou.rayyuan.ebooksearchtaiwan.composable.iconpack.BaselineCheck24Px
import liou.rayyuan.ebooksearchtaiwan.composable.iconpack.EBookIcons
import liou.rayyuan.ebooksearchtaiwan.composable.rememberDragDropState
import liou.rayyuan.ebooksearchtaiwan.composable.toMutableStateList
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import java.util.Collections
import liou.rayyuan.ebooksearchtaiwan.utils.DeviceVibrateHelper
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookStoreReorderScreen(
    viewModel: BookStoreReorderViewModel,
    modifier: Modifier = Modifier,
    deviceVibrateHelper: DeviceVibrateHelper = koinInject(),
    onNavigationBack: () -> Unit = {},
    onSaveSettings: () -> Unit = {}
) {
    var showSaveSettingIcon by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.activty_reorder_title),
                        style =
                            TextStyle.Default.copy(
                                color = EBookTheme.colors.colorOnPrimary,
                                fontSize = 22.sp
                            )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigationBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Button",
                            tint = EBookTheme.colors.colorOnPrimary
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors().copy(
                        containerColor = EBookTheme.colors.colorBackground,
                        scrolledContainerColor = EBookTheme.colors.colorBackground
                    ),
                actions = {
                    if (showSaveSettingIcon) {
                        IconButton(
                            onClick = onSaveSettings,
                            colors =
                                IconButtonDefaults.iconButtonColors().copy(
                                    contentColor = EBookTheme.colors.colorOnPrimary
                                )
                        ) {
                            Icon(EBookIcons.BaselineCheck24Px, contentDescription = "Send Result Button")
                        }
                    }
                }
            )
        },
        containerColor = EBookTheme.colors.colorBackground,
        modifier = modifier,
    ) { innerPaddings ->
        val sortedStores = viewModel.sortedStores.collectAsStateWithLifecycle().value
        if (sortedStores.isNotEmpty()) {
            Box(
                modifier = Modifier.padding(innerPaddings)
            ) {
                BookStoreReorderContent(
                    sortedStores = sortedStores,
                    modifier = Modifier,
                    onShowSaveSetting = { show ->
                        showSaveSettingIcon = show
                    },
                    updateBookStoreSort = { bookStores ->
                        viewModel.currentBookStoreSort = bookStores
                    },
                    onStartMoving = {
                        deviceVibrateHelper.vibrate(50L)
                    }
                )
            }
        }
    }
}

@Composable
private fun BookStoreReorderContent(
    sortedStores: ImmutableList<SortedStore>,
    modifier: Modifier = Modifier,
    contentPaddings: PaddingValues = PaddingValues(),
    updateBookStoreSort: (bookStores: SnapshotStateList<SortedStore>) -> Unit = {},
    onShowSaveSetting: (show: Boolean) -> Unit = {},
    onStartMoving: () -> Unit = {}
) {
    var isMovingListItem by remember { mutableStateOf(false) }

    val bookStores = remember(sortedStores) { sortedStores.toMutableStateList() }
    updateBookStoreSort(bookStores)

    val draggableItemCounts by remember(sortedStores) {
        derivedStateOf { bookStores.size }
    }
    val enableStoreCounts = bookStores.count { it.isEnable.value }

    val listState = rememberLazyListState()
    val dragDropState =
        rememberDragDropState(
            lazyListState = listState,
            onMove = { fromIndex, toIndex ->
                Collections.swap(bookStores, fromIndex, toIndex)
                onShowSaveSetting(true)
            },
            onMoveStart = {
                isMovingListItem = true
                onStartMoving()
            },
            onMoveInterrupt = {
                isMovingListItem = false
            },
            draggableItemCounts = draggableItemCounts
        )

    dragDropState.isEnable = enableStoreCounts > 1

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = contentPaddings,
        state = listState,
        modifier = modifier.dragContainer(dragDropState)
    ) {
        draggableItems(bookStores, dragDropState) { modifier, sortedStore, _ ->
            BookStoreOrderItem(
                sortedStore = sortedStore,
                modifier = modifier,
                showCheckBox = !isMovingListItem,
                disableCheckBox = (enableStoreCounts < 2 && sortedStore.isEnable.value),
                enableDragging = enableStoreCounts > 1,
                onVisibilityChange = {
                    onShowSaveSetting(true)
                }
            )
        }
    }
}
