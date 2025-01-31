package liou.rayyuan.ebooksearchtaiwan.bookstorereorder

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import liou.rayyuan.ebooksearchtaiwan.R
import com.rayliu.commonmain.data.DefaultStoreNames
import java.util.Collections
import liou.rayyuan.ebooksearchtaiwan.view.ListDraggingViewHolderHelper
import liou.rayyuan.ebooksearchtaiwan.view.ListItemTouchListener
import liou.rayyuan.ebooksearchtaiwan.view.OnBookStoreItemChangedListener
import liou.rayyuan.ebooksearchtaiwan.view.getStringResource

class BookstoreNameAdapter(
    private var listener: OnBookStoreItemChangedListener?
) : RecyclerView.Adapter<BookstoreNameAdapter.BookstoreViewHolder>(),
    ListItemTouchListener {
    private val bookStores = mutableListOf<SortedStore>()
    private val payloadCheckBoxStatus = "payload-check-box-status"

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookstoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_bookstores, parent, false)
        return BookstoreViewHolder(view)
    }

    override fun getItemCount(): Int = bookStores.size

    override fun onBindViewHolder(
        holder: BookstoreViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        }

        if (payloads.contains(payloadCheckBoxStatus)) {
            val bookStore = bookStores[position]
            toggleEdition(holder, bookStore)
        }
    }

    override fun onBindViewHolder(
        holder: BookstoreViewHolder,
        position: Int
    ) {
        val index = holder.absoluteAdapterPosition
        if (index == RecyclerView.NO_POSITION) {
            return
        }

        val bookStore = bookStores[index]
        val storeName = bookStore.defaultStoreName
        val context = holder.itemView.context
        holder.bookstoreTitle.text = context.getString(storeName.getStringResource())
        toggleEdition(holder, bookStore)

        if (bookStore.isVisible) {
            holder.bookstoreCheckBox.isChecked = true
        } else {
            holder.bookstoreCheckBox.isChecked = false
        }
        holder.bookstoreCheckBox.jumpDrawablesToCurrentState()
    }

    private fun toggleEdition(
        holder: BookstoreViewHolder,
        bookStore: SortedStore
    ) {
        val enableStoreCounts = bookStores.count { it.isVisible }
        toggleDraggable(holder, enableStoreCounts > 1)

        val disableCheckBox = (enableStoreCounts < 2 && bookStore.isVisible)
        toggleVisibilityChange(holder, bookStore, !disableCheckBox)
    }

    private fun toggleVisibilityChange(
        viewHolder: BookstoreViewHolder,
        bookStore: SortedStore,
        enable: Boolean
    ) {
        if (enable) {
            viewHolder.bookstoreCheckBox.isEnabled = true
            viewHolder.bookstoreCheckBox.isClickable = true
            viewHolder.bookstoreCheckBox.alpha = 1.0f
            viewHolder.bookstoreCheckBox.setOnClickListener {
                viewHolder.bookstoreCheckBox.statusChange(bookStore)
            }
            viewHolder.bookstoreTouchZone.setOnClickListener {
                viewHolder.bookstoreCheckBox.statusChange(bookStore)
            }
        } else {
            viewHolder.bookstoreCheckBox.isEnabled = false
            viewHolder.bookstoreCheckBox.isClickable = false
            viewHolder.bookstoreCheckBox.alpha = 0.4f
            viewHolder.bookstoreCheckBox.setOnClickListener(null)
            viewHolder.bookstoreTouchZone.setOnClickListener(null)
        }
    }

    private fun AppCompatCheckBox.statusChange(bookstore: SortedStore) {
        val switchToResult = !isChecked
        isChecked = switchToResult
        handleCheckChanged(switchToResult, bookstore)
    }

    private fun handleCheckChanged(
        isChecked: Boolean,
        bookStore: SortedStore
    ) {
        listener?.onStoreVisibilityChanged()
        if (isChecked) {
            displayStoreFromResult(bookStore)
        } else {
            removeDisplayStoreFromResult(bookStore)
        }
    }

    @SuppressLint("ClickableViewAccessibility") // ignore onTouchListener implemented performClick request
    private fun toggleDraggable(viewHolder: BookstoreViewHolder, enable: Boolean) {
        if (enable) {
            viewHolder.bookstoreReorderImage.isEnabled = true
            viewHolder.bookstoreReorderImage.isClickable = true
            viewHolder.bookstoreReorderImage.alpha = 1.0f
            viewHolder.bookstoreReorderImage.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    listener?.onStartDrag(viewHolder)
                }

                false
            }
        } else {
            viewHolder.bookstoreReorderImage.isEnabled = false
            viewHolder.bookstoreReorderImage.isClickable = false
            viewHolder.bookstoreReorderImage.alpha = 0.2f
            viewHolder.bookstoreReorderImage.setOnTouchListener(null)
        }
    }

    fun setStoreNames(displayStores: List<DefaultStoreNames>) {
        Log.i("BookstoreNameAdapter", "display stores = $displayStores")
        val disableStores = DefaultStoreNames.values().toMutableList()
        disableStores.remove(DefaultStoreNames.BEST_RESULT)
        disableStores.remove(DefaultStoreNames.UNKNOWN)
        disableStores.removeAll(displayStores)

        if (disableStores.isNotEmpty()) {
            Log.i("BookstoreNameAdapter", "disabled bookstore are = $disableStores")
            val disabledList =
                disableStores.map {
                    SortedStore(it, false)
                }

            val bookStores =
                displayStores.map {
                    SortedStore(it, true)
                }.toMutableList()
            bookStores.addAll(disabledList)
            this.bookStores.addAll(bookStores)
        } else {
            val bookStores =
                displayStores.map {
                    SortedStore(it, true)
                }
            this.bookStores.addAll(bookStores)
        }
        notifyDataSetChanged()
    }

    fun getStoreNames(): List<DefaultStoreNames> =
        bookStores.filter {
            it.isVisible
        }.map {
            it.defaultStoreName
        }

    private fun displayStoreFromResult(store: SortedStore) {
        if (!store.isVisible) {
//            store.isVisible = true
            notifyItemRangeChanged(0, bookStores.size, payloadCheckBoxStatus)
        }
    }

    private fun removeDisplayStoreFromResult(store: SortedStore): Boolean {
        val visibleCounts = bookStores.count { it.isVisible }
        if (store.isVisible && visibleCounts > 1) {
//            store.isVisible = false
            notifyItemRangeChanged(0, bookStores.size, payloadCheckBoxStatus)
            return true
        }
        return false
    }

    override fun onItemMove(
        fromPosition: Int,
        toPosition: Int
    ) {
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

    fun release() {
        listener = null
    }

    class BookstoreViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView),
        ListDraggingViewHolderHelper {
        val bookstoreTitle: TextView = itemView.findViewById(R.id.adapter_bookstore_name_textview)
        val bookstoreTouchZone: View = itemView.findViewById(R.id.adapter_bookstore_touchzone)
        val bookstoreReorderImage: ImageView = itemView.findViewById(R.id.adapter_bookstore_reorder_imageview)
        val bookstoreCheckBox: MaterialCheckBox = itemView.findViewById(R.id.adapter_bookstore_name_checkbox)

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
