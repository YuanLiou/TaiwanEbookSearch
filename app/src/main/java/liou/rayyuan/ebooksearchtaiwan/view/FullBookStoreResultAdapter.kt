package liou.rayyuan.ebooksearchtaiwan.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.model.entity.Book
import liou.rayyuan.ebooksearchtaiwan.utils.getLocalizedName

/**
 * Created by louis383 on 2018/1/7.
 */
class FullBookStoreResultAdapter(private val clickHandler: BookResultClickHandler):
        RecyclerView.Adapter<RecyclerView.ViewHolder>(),
        BookResultClickHandler {

    private val header = 1001

    private val results = mutableListOf<BookResultView>()
    private var pool: RecyclerView.RecycledViewPool? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == header) {
            MobileAds.initialize(parent.context, BuildConfig.AD_MOB_ID)
            val headerView: View = LayoutInflater.from(parent.context).inflate(R.layout.admob_view_header, parent, false)
            return BookStoreResultHeaderViewHolder(headerView)
        }

        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.adapter_bookstore_result, parent, false)
        return BookStoreResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BookStoreResultHeaderViewHolder) {
            return
        }

        val index: Int = holder.adapterPosition - 1    // minor a position for header
        if (holder is BookStoreResultViewHolder && results.isNotEmpty() && index < results.size) {
            val result: BookResultView = results[index]
            val adapter: BookResultAdapter = result.adapter
            holder.bookStoreTitle.text = result.defaultStoreName.getLocalizedName(holder.itemView.context)

            adapter.bookResultClickHandler = this
            with(holder) {
                bookStoreResult.visibility = View.VISIBLE
                bookStoreResultIsEmpty.visibility = View.GONE
                bookStoreResult.adapter = adapter
            }

            if (result.adapter.isBookEmpty()) {
                holder.bookStoreResultIsEmpty.visibility = View.VISIBLE
            } else {
                holder.bookStoreResultIsEmpty.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        if (results.isEmpty()) {
            return 0
        }

        return results.size + 1    // plus a position for header
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return header
        }

        return super.getItemViewType(position)
    }

    fun addResult(result: BookResultView) {
        results.add(result)
        notifyItemInserted(results.size)
    }

    fun addResultToBeginning(result: BookResultView) {
        results.add(0, result)
        notifyItemInserted(0)
    }

    fun clean() {
        results.clear()
        notifyDataSetChanged()
    }

    override fun onBookCardClicked(book: Book) {
        clickHandler.onBookCardClicked(book)
    }

    inner class BookStoreResultViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        internal val bookStoreTitle: TextView = itemView.findViewById(R.id.search_result_subtitle_top)
        internal val bookStoreResult: RecyclerView = itemView.findViewById(R.id.search_result_list_top)
        internal val bookStoreResultIsEmpty: TextView = itemView.findViewById(R.id.search_result_list_empty)

        init {
            if (pool == null) {
                pool = bookStoreResult.recycledViewPool
            }
            bookStoreResult.setRecycledViewPool(pool)
            (bookStoreResult.layoutManager as LinearLayoutManager).recycleChildrenOnDetach = true
        }
    }

    class BookStoreResultHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookResultAdView: AdView = itemView.findViewById(R.id.admob_view_header_adview)

        init {
            val adRequestBuilder = AdRequest.Builder()
            val adRequest = adRequestBuilder.build()
            bookResultAdView.loadAd(adRequest)
        }
    }
}