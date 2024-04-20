package liou.rayyuan.ebooksearchtaiwan.view

import androidx.recyclerview.widget.RecyclerView

interface OnBookStoreItemChangedListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)

    fun onStoreVisibilityChanged()
}
