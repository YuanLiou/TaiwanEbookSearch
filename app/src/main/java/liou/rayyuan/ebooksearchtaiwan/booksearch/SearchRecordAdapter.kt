package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import liou.rayyuan.ebooksearchtaiwan.BR
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.model.entity.SearchRecord

class SearchRecordAdapter(private var listener: OnSearchRecordsClickListener?): PagedListAdapter<SearchRecord,
        SearchRecordAdapter.SearchRecordViewHolder>(SearchRecordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRecordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItemBinding: ViewDataBinding = DataBindingUtil.inflate(inflater,
                R.layout.adapter_search_record, parent, false)
        return SearchRecordViewHolder(listItemBinding, listener)
    }

    override fun onBindViewHolder(holder: SearchRecordViewHolder, position: Int) {
        val searchRecord = getItem(position)
        searchRecord?.let {
            holder.bind(it)
        }
    }

    fun addItems(searchRecords: PagedList<SearchRecord>?) {
        submitList(searchRecords)
        notifyDataSetChanged()
    }

    fun release() {
        listener = null
    }

    class SearchRecordDiffCallback: DiffUtil.ItemCallback<SearchRecord>() {
        override fun areItemsTheSame(oldItem: SearchRecord, newItem: SearchRecord): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SearchRecord, newItem: SearchRecord): Boolean {
            return oldItem == newItem
        }
    }

    class SearchRecordViewHolder(private val itemBindingView: ViewDataBinding,
                                 private val listener: OnSearchRecordsClickListener?): RecyclerView.ViewHolder(itemBindingView.root) {
        internal val searchRecordText: TextView = itemView.findViewById(R.id.adapter_search_record_text)
        internal val searchRecordCloseImage: ImageView = itemView.findViewById(R.id.adapter_search_record_close_image)

        internal fun bind(searchRecord: SearchRecord) {
            itemBindingView.setVariable(BR.search_record, searchRecord)
            itemBindingView.executePendingBindings()

            itemView.setOnClickListener { listener?.onSearchRecordClicked(searchRecord) }
            searchRecordCloseImage.setOnClickListener { listener?.onSearchRecordCloseImageClicked(searchRecord, adapterPosition) }
        }
    }

    interface OnSearchRecordsClickListener {
        fun onSearchRecordClicked(searchRecord: SearchRecord)
        fun onSearchRecordCloseImageClicked(searchRecord: SearchRecord, position: Int)
    }

}