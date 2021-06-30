package liou.rayyuan.ebooksearchtaiwan.bookstorereorder

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.R
import com.rayliu.commonmain.UserPreferenceManager
import com.rayliu.commonmain.Utils
import com.rayliu.commonmain.domain.usecase.GetDefaultBookSortUseCase
import liou.rayyuan.ebooksearchtaiwan.utils.bindView
import liou.rayyuan.ebooksearchtaiwan.view.ListItemTouchCallback
import liou.rayyuan.ebooksearchtaiwan.view.OnBookStoreItemChangedListener
import org.koin.android.ext.android.inject

class BookStoreReorderActivity : BaseActivity(R.layout.activity_reorder_stores), OnBookStoreItemChangedListener {

    private val toolbar: Toolbar by bindView(R.id.activity_reorder_layout_toolbar)
    private val recyclerView: RecyclerView by bindView(R.id.activity_reorder_recyclerview)
    private val adapter: BookstoreNameAdapter = BookstoreNameAdapter(this)
    private val preferenceManager: UserPreferenceManager by inject()
    private val getDefaultBookSortUseCase: GetDefaultBookSortUseCase by inject()

    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var checkMarkerOption: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        with(recyclerView) {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(this@BookStoreReorderActivity,
                    LinearLayoutManager.VERTICAL))
            adapter = this@BookStoreReorderActivity.adapter
        }

        val bookstores = getDefaultBookSortUseCase()
        adapter.setStoreNames(bookstores)

        val listItemTouchCallback = ListItemTouchCallback(adapter)
        itemTouchHelper = ItemTouchHelper(listItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

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
            checkMarkerOption.icon.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
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
                preferenceManager.saveBookStoreSort(result)
                eventTracker.logTopSelectedStoreName(result)
                finish()
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
}