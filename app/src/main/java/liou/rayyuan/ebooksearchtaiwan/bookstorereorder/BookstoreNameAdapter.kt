package liou.rayyuan.ebooksearchtaiwan.bookstorereorder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.utils.DefaultStoreNames

class BookstoreNameAdapter: RecyclerView.Adapter<BookstoreNameAdapter.BookstoreViewHolder>() {

    private val bookStores = mutableListOf<DefaultStoreNames>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookstoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_bookstores, parent, false)
        return BookstoreViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookStores.size
    }

    override fun onBindViewHolder(holder: BookstoreViewHolder, position: Int) {
        val index = holder.adapterPosition
        if (index != RecyclerView.NO_POSITION) {
            val storeName = bookStores[index]
            val context = holder.itemView.context
            holder.bookstoreTitle.text = context.getString(storeName.defaultResId)
        }
    }

    fun setStoreNames(storeNames: List<DefaultStoreNames>) {
        this.bookStores.addAll(storeNames)
        notifyDataSetChanged()
    }

    class BookstoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val bookstoreTitle = itemView.findViewById<TextView>(R.id.adapter_bookstore_name_textview)
        internal val bookstoreTouchZone = itemView.findViewById<View>(R.id.adapter_bookstore_touchzone)
        internal val bookstoreReorderImage = itemView.findViewById<ImageView>(R.id.adapter_bookstore_reorder_imageview)
    }

}