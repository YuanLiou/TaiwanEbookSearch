package liou.rayyuan.ebooksearchtaiwan.bookstorereorder

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.model.UserPreferenceManager
import liou.rayyuan.ebooksearchtaiwan.utils.Utils
import liou.rayyuan.ebooksearchtaiwan.utils.bindView
import org.koin.android.ext.android.inject

class BookStoreReorderActivity : BaseActivity() {

    private val toolbar: Toolbar by bindView(R.id.activity_reorder_layout_toolbar)
    private val recyclerView: RecyclerView by bindView(R.id.activity_reorder_recyclerview)
    private val adapter: BookstoreNameAdapter = BookstoreNameAdapter()
    private val preferenceManager: UserPreferenceManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reorder_stores)
        initToolbar()

        with(recyclerView) {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(this@BookStoreReorderActivity,
                    LinearLayoutManager.VERTICAL))
            adapter = this@BookStoreReorderActivity.adapter
        }

        val bookstores = preferenceManager.getBookStoreSort()
        bookstores?.let {
            adapter.setStoreNames(it)
        } ?: run {
            val defaultSort = Utils.getDefaultSort()
            preferenceManager.saveBookStoreSort(defaultSort)
            adapter.setStoreNames(defaultSort)
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.pure_white))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}