package liou.rayyuan.ebooksearchtaiwan.bookstorereorder

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.R
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.arch.IView
import liou.rayyuan.ebooksearchtaiwan.bookstorereorder.composable.BookStoreOrderItem
import liou.rayyuan.ebooksearchtaiwan.composable.toMutableStateList
import liou.rayyuan.ebooksearchtaiwan.databinding.ActivityReorderStoresBinding
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.utils.ActivityViewBinding
import liou.rayyuan.ebooksearchtaiwan.utils.bindView
import liou.rayyuan.ebooksearchtaiwan.utils.setupEdgeToEdge
import liou.rayyuan.ebooksearchtaiwan.view.ListItemTouchCallback
import liou.rayyuan.ebooksearchtaiwan.view.OnBookStoreItemChangedListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookStoreReorderActivity :
    BaseActivity(R.layout.activity_reorder_stores),
    OnBookStoreItemChangedListener,
    IView<BookStoreReorderViewState> {
    private val viewModel: BookStoreReorderViewModel by viewModel()
    private val viewBinding: ActivityReorderStoresBinding by ActivityViewBinding(
        ActivityReorderStoresBinding::bind,
        R.id.activity_reorder_layout_rootView
    )

    private val toolbar: Toolbar by bindView(R.id.activity_reorder_layout_toolbar)
    private val adapter: BookstoreNameAdapter = BookstoreNameAdapter(this)

    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var checkMarkerOption: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        val listItemTouchCallback = ListItemTouchCallback(adapter)
        itemTouchHelper = ItemTouchHelper(listItemTouchCallback)
        viewModel.viewState.observe(this) { state -> render(state) }
        sendUserIntent(BookStoreReorderUserIntent.GetPreviousSavedSort)
        setupEdgeToEdge()

        val composeView = findViewById<ComposeView>(R.id.activity_reorder_composeView)
        with(composeView) {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                EBookTheme(
                    darkTheme = isDarkTheme()
                ) {
                    val sortedStores = viewModel.sortedStores.collectAsStateWithLifecycle().value
                    val bookStores = remember(sortedStores) { sortedStores.toMutableStateList() }
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(bookStores, key = { it.defaultStoreName }) { sortedStore ->
                            BookStoreOrderItem(
                                sortedStore = sortedStore
                            )
                        }
                    }
                }
            }
        }
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

    override fun onDestroy() {
        adapter.release()
        super.onDestroy()
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
                val result = adapter.getStoreNames()
                eventTracker.logTopSelectedStoreName(result)
                sendUserIntent(BookStoreReorderUserIntent.UpdateSort(result))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //region OnBookStoreItemChangedListener
    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        if (this::itemTouchHelper.isInitialized) {
            itemTouchHelper.startDrag(viewHolder)
        }
        showSaveSettingIcon()
    }

    override fun onStoreVisibilityChanged() {
        showSaveSettingIcon()
    }

    private fun showSaveSettingIcon() {
        if (this::checkMarkerOption.isInitialized) {
            checkMarkerOption.isVisible = true
        }
    }

    //endregion
    override fun render(viewState: BookStoreReorderViewState) {
        when (viewState) {
            BookStoreReorderViewState.BackToPreviousPage -> {
                finish()
            }

            is BookStoreReorderViewState.PrepareBookSort -> {
                adapter.setStoreNames(viewState.bookSort)
            }
        }
    }

    private fun sendUserIntent(userIntent: BookStoreReorderUserIntent) {
        lifecycleScope.launch {
            viewModel.userIntents.emit(userIntent)
        }
    }
}
