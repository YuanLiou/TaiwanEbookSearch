package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rayliu.commonmain.domain.model.SearchRecord
import liou.rayyuan.ebooksearchtaiwan.R

class SearchRecordAdapter(
    private var listener: OnSearchRecordsClickListener?
) : PagingDataAdapter<
        SearchRecord,
        SearchRecordAdapter.SearchRecordViewHolder
        >(SearchRecordDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchRecordViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_search_record, parent, false)
        return SearchRecordViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(
        holder: SearchRecordViewHolder,
        position: Int
    ) {
        val searchRecord = getItem(position)
        searchRecord?.let {
            holder.bind(it)
        }
    }

    fun addItems(
        lifecycle: Lifecycle,
        searchRecords: PagingData<SearchRecord>
    ) {
        submitData(lifecycle, searchRecords)
        notifyDataSetChanged()
    }

    fun release() {
        listener = null
    }

    class SearchRecordDiffCallback : DiffUtil.ItemCallback<SearchRecord>() {
        override fun areItemsTheSame(
            oldItem: SearchRecord,
            newItem: SearchRecord
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: SearchRecord,
            newItem: SearchRecord
        ): Boolean = oldItem == newItem
    }

    class SearchRecordViewHolder(
        itemView: View,
        private val listener: OnSearchRecordsClickListener?
    ) : RecyclerView.ViewHolder(itemView) {
        private val searchRecordText: TextView = itemView.findViewById(R.id.adapter_search_record_text)
        private val searchRecordCloseImage: ImageView = itemView.findViewById(R.id.adapter_search_record_close_image)

        fun bind(searchRecord: SearchRecord) {
            searchRecordText.text = searchRecord.text
            itemView.setOnClickListener { listener?.onSearchRecordClicked(searchRecord) }
            searchRecordCloseImage.setOnClickListener { listener?.onSearchRecordCloseImageClicked(searchRecord, absoluteAdapterPosition) }
        }
    }

    interface OnSearchRecordsClickListener {
        fun onSearchRecordClicked(searchRecord: SearchRecord)

        fun onSearchRecordCloseImageClicked(
            searchRecord: SearchRecord,
            position: Int
        )
    }
}
