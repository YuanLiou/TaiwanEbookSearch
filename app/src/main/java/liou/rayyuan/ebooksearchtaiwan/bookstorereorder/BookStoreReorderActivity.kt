package liou.rayyuan.ebooksearchtaiwan.bookstorereorder

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStarted
import kotlinx.coroutines.flow.collectLatest
import java.util.Collections
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.R
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.bookstorereorder.composable.BookStoreOrderItem
import liou.rayyuan.ebooksearchtaiwan.composable.dragContainer
import liou.rayyuan.ebooksearchtaiwan.composable.draggableItems
import liou.rayyuan.ebooksearchtaiwan.composable.rememberDragDropState
import liou.rayyuan.ebooksearchtaiwan.composable.toMutableStateList
import liou.rayyuan.ebooksearchtaiwan.databinding.ActivityReorderStoresBinding
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.utils.ActivityViewBinding
import liou.rayyuan.ebooksearchtaiwan.utils.bindView
import liou.rayyuan.ebooksearchtaiwan.utils.setupEdgeToEdge
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookStoreReorderActivity : BaseActivity(R.layout.activity_reorder_stores) {
    private val viewModel: BookStoreReorderViewModel by viewModel()
    private val viewBinding: ActivityReorderStoresBinding by ActivityViewBinding(
        ActivityReorderStoresBinding::bind,
        R.id.activity_reorder_layout_rootView
    )

    private val toolbar: Toolbar by bindView(R.id.activity_reorder_layout_toolbar)

    private lateinit var checkMarkerOption: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupEdgeToEdge()
        initToolbar()

        val composeView = findViewById<ComposeView>(R.id.activity_reorder_composeView)
        with(composeView) {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                EBookTheme(
                    darkTheme = isDarkTheme()
                ) {
                    val sortedStores = viewModel.sortedStores.collectAsStateWithLifecycle().value
                    if (sortedStores.isNotEmpty()) {
                        var isMovingListItem by remember { mutableStateOf(false) }

                        val bookStores = remember(sortedStores) { sortedStores.toMutableStateList() }
                        viewModel.currentBookStoreSort = bookStores

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
                                    showSaveSettingIcon()
                                },
                                onMoveStart = {
                                    isMovingListItem = true
                                },
                                onMoveInterrupt = {
                                    isMovingListItem = false
                                },
                                draggableItemCounts = draggableItemCounts
                            )

                        dragDropState.isEnable = enableStoreCounts > 1

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            state = listState,
                            modifier = Modifier.dragContainer(dragDropState)
                        ) {
                            draggableItems(bookStores, dragDropState) { modifier, sortedStore, _ ->
                                BookStoreOrderItem(
                                    sortedStore = sortedStore,
                                    modifier = modifier,
                                    showCheckBox = !isMovingListItem,
                                    disableCheckBox = (enableStoreCounts < 2 && sortedStore.isEnable.value),
                                    enableDragging = enableStoreCounts > 1,
                                    onVisibilityChange = {
                                        showSaveSettingIcon()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Render Book Result State
        lifecycleScope.launch {
            withStarted(block = {})
            viewModel.viewState.collectLatest { state ->
                if (state != null) {
                    render(state)
                }
            }
        }
        viewModel.getPreviousSavedBookResultSort()
    }

    private fun setupEdgeToEdge() {
        viewBinding.root.setupEdgeToEdge()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.reorder_page, menu)
        val checkMarkerOption = menu.findItem(R.id.reorder_page_menu_action_check)
        if (!isDarkTheme()) {
            checkMarkerOption.icon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    ContextCompat.getColor(this, R.color.darker_gray_3B),
                    BlendModeCompat.SRC_ATOP
                )
        }
        this.checkMarkerOption = checkMarkerOption
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.reorder_page_menu_action_check -> {
                val result = viewModel.getStoreNames()
                if (result != null) {
                    eventTracker.logTopSelectedStoreName(result)
                    viewModel.updateCurrentSort(result)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSaveSettingIcon() {
        if (this::checkMarkerOption.isInitialized) {
            checkMarkerOption.isVisible = true
        }
    }

    private fun render(viewState: BookStoreReorderViewState) {
        when (viewState) {
            BookStoreReorderViewState.BackToPreviousPage -> {
                finish()
            }
        }
    }
}
