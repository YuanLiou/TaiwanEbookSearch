package liou.rayyuan.ebooksearchtaiwan.composable

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.channels.Channel

data class DraggableItem(
    val index: Int
)

inline fun <T : Any> LazyListScope.draggableItems(
    items: List<T>,
    dragDropState: DragDropState,
    crossinline content: @Composable (Modifier, T, isDragging: Boolean) -> Unit
) {
    itemsIndexed(
        items = items,
        contentType = { index, _ -> DraggableItem(index) }
    ) { index, item ->
        val isDragging = dragDropState.draggingItemIndex == index
        val modifier =
            if (isDragging) {
                Modifier
                    .zIndex(1f)
                    .graphicsLayer {
                        translationY = dragDropState.delta
                    }
                    .shadow(4.dp)
            } else {
                Modifier
            }
        content(modifier, item, isDragging)
    }
}

fun Modifier.dragContainer(dragDropState: DragDropState): Modifier =
    this.then(
        pointerInput(dragDropState) {
            detectDragGesturesAfterLongPress(
                onDrag = { change, offset ->
                    change.consume()
                    dragDropState.onDrag(offset = offset)
                },
                onDragStart = { offset -> dragDropState.onDragStart(offset) },
                onDragEnd = { dragDropState.onDragInterrupted() },
                onDragCancel = { dragDropState.onDragInterrupted() }
            )
        }
    )

@Composable
fun rememberDragDropState(
    lazyListState: LazyListState,
    onMove: (Int, Int) -> Unit,
    draggableItemCounts: Int,
    onMoveStart: () -> Unit = {},
    onMoveInterrupt: () -> Unit = {}
): DragDropState {
    val state =
        remember(lazyListState) {
            DragDropState(
                draggableItemCounts = draggableItemCounts,
                stateList = lazyListState,
                onMove = onMove,
                onMoveStart = onMoveStart,
                onMoveInterrupted = onMoveInterrupt,
                isEnable = true
            )
        }

    LaunchedEffect(state) {
        while (true) {
            val diff = state.scrollChannel.receive()
            lazyListState.scrollBy(diff)
        }
    }
    return state
}

class DragDropState(
    private val draggableItemCounts: Int,
    private val stateList: LazyListState,
    private val onMove: (Int, Int) -> Unit,
    private val onMoveStart: () -> Unit = {},
    private val onMoveInterrupted: () -> Unit = {},
    var isEnable: Boolean = true
) {
    var draggingItemIndex: Int? by mutableStateOf(null)
        private set
    var delta by mutableFloatStateOf(0f)
        private set
    val scrollChannel = Channel<Float>()
    private var draggingItem: LazyListItemInfo? = null

    internal fun onDragStart(offset: Offset) {
        if (!isEnable) {
            return
        }

        stateList.layoutInfo.visibleItemsInfo
            .firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }
            ?.also {
                (it.contentType as? DraggableItem)?.let { draggableItem ->
                    draggingItem = it
                    draggingItemIndex = draggableItem.index
                    onMoveStart()
                }
            }
    }

    internal fun onDragInterrupted() {
        draggingItem = null
        draggingItemIndex = null
        delta = 0f
        onMoveInterrupted()
    }

    internal fun onDrag(offset: Offset) {
        if (!isEnable) {
            return
        }

        delta += offset.y

        val currentDraggingItemIndex = draggingItemIndex ?: return
        val currentDraggingItem = draggingItem ?: return

        val startOffset = currentDraggingItem.offset + delta
        val endOffset = currentDraggingItem.offset + currentDraggingItem.size + delta
        val middleOffset = startOffset + (endOffset - startOffset) / 2

        val targetItem =
            stateList.layoutInfo.visibleItemsInfo
                .find { item ->
                    middleOffset.toInt() in item.offset..item.offset + item.size &&
                        currentDraggingItem.index != item.index &&
                        item.contentType is DraggableItem
                }

        if (targetItem != null) {
            val targetIndex = (targetItem.contentType as DraggableItem).index
            onMove(currentDraggingItemIndex, targetIndex)
            draggingItemIndex = targetIndex
            delta += currentDraggingItem.offset - targetItem.offset
            draggingItem = targetItem
        } else {
            val startOffsetToTop = startOffset - stateList.layoutInfo.viewportStartOffset
            val endOffsetToTop = endOffset - stateList.layoutInfo.viewportEndOffset
            val scroll =
                when {
                    startOffsetToTop < 0 -> startOffsetToTop.coerceAtMost(0f)
                    endOffsetToTop > 0 -> endOffsetToTop.coerceAtLeast(0f)
                    else -> 0f
                }

            if (scroll != 0f &&
                currentDraggingItemIndex != 0 &&
                draggableItemCounts > 0 &&
                currentDraggingItemIndex != draggableItemCounts - 1
            ) {
                scrollChannel.trySend(scroll)
            }
        }
    }
}
