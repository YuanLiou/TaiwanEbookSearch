package liou.rayyuan.ebooksearchtaiwan.view

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.model.entity.Book

/**
 * Created by louis383 on 2018/1/7.
 */
class FullBookStoreResultAdapter(private val clickHandler: BookResultClickHandler):
        RecyclerView.Adapter<FullBookStoreResultAdapter.BookStoreResultViewHolder>(),
        BookResultClickHandler {

    private val results = mutableListOf<BookResultView>()
    private var pool: RecyclerView.RecycledViewPool? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookStoreResultViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.adapter_bookstore_result, parent, false)
        return BookStoreResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookStoreResultViewHolder, position: Int) {
        val index: Int = holder.adapterPosition
        if (results.isNotEmpty() && index < results.size) {
            val result: BookResultView = results[index]
            val adapter: BookResultAdapter = result.adapter
            holder.bookStoreTitle.text = result.title

            if (result.adapter.getBooksCount() > 0) {
                adapter.bookResultClickHandler = this
                with(holder) {
                    bookStoreResult.visibility = View.VISIBLE
                    bookStoreResultIsEmpty.visibility = View.GONE

                    if (pool == null) {
                        pool = bookStoreResult.recycledViewPool
                    }

                    bookStoreResult.recycledViewPool = pool
                    (bookStoreResult.layoutManager as LinearLayoutManager).recycleChildrenOnDetach = true
                    bookStoreResult.adapter = adapter
                }

            } else {
                holder.bookStoreResult.visibility = View.GONE
                holder.bookStoreResultIsEmpty.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int = results.size

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

    class BookStoreResultViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        internal val bookStoreTitle: TextView = itemView.findViewById(R.id.search_result_subtitle_top)
        internal val bookStoreResult: RecyclerView = itemView.findViewById(R.id.search_result_list_top)
        internal val bookStoreResultIsEmpty: TextView = itemView.findViewById(R.id.search_result_list_empty)
    }
}