package liou.rayyuan.ebooksearchtaiwan.bookstorereorder

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.utils.DefaultStoreNames
import liou.rayyuan.ebooksearchtaiwan.view.ListDraggingViewHolderHelper
import liou.rayyuan.ebooksearchtaiwan.view.ListItemTouchListener
import liou.rayyuan.ebooksearchtaiwan.view.OnStartDragListener
import java.util.*

class BookstoreNameAdapter(private val listener: OnStartDragListener): RecyclerView.Adapter<BookstoreNameAdapter.BookstoreViewHolder>(),
        ListItemTouchListener {

    private val bookStores = mutableListOf<DefaultStoreNames>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookstoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_bookstores, parent, false)
        return BookstoreViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookStores.size
    }

    @SuppressLint("ClickableViewAccessibility")    // ignore onTouchListener implemented performClick request
    override fun onBindViewHolder(holder: BookstoreViewHolder, position: Int) {
        val index = holder.adapterPosition
        if (index != RecyclerView.NO_POSITION) {
            val storeName = bookStores[index]
            val context = holder.itemView.context
            holder.bookstoreTitle.text = context.getString(storeName.defaultResId)

            holder.bookstoreReorderImage.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                        listener.onStartDrag(holder)
                    }

                    return false
                }
            })
        }
    }

    fun setStoreNames(storeNames: List<DefaultStoreNames>) {
        this.bookStores.addAll(storeNames)
        notifyDataSetChanged()
    }

    fun getStoreNames(): List<DefaultStoreNames> {
        return bookStores
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(bookStores, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(bookStores, i, i - 1)
            }
        }

        notifyItemMoved(fromPosition, toPosition)
        Log.i("BookstoreNameAdapter", "onItemMove, list is = $bookStores")
    }

    class BookstoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ListDraggingViewHolderHelper {
        internal val bookstoreTitle = itemView.findViewById<TextView>(R.id.adapter_bookstore_name_textview)
        internal val bookstoreTouchZone = itemView.findViewById<View>(R.id.adapter_bookstore_touchzone)
        internal val bookstoreReorderImage = itemView.findViewById<ImageView>(R.id.adapter_bookstore_reorder_imageview)

        override fun onListItemSelected() {
            ViewCompat.setElevation(itemView, 8f)
            ViewCompat.setTranslationZ(itemView, 8f)
        }

        override fun onListItemCleared() {
            ViewCompat.setElevation(itemView, 0f)
            ViewCompat.setTranslationZ(itemView, 0f)
        }
    }

}