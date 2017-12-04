package liou.rayyuan.ebooksearchtaiwan.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.model.entity.Book
import liou.rayyuan.ebooksearchtaiwan.view.BookResultAdapter.BookResultViewHolder
import liou.rayyuan.ebooksearchtaiwan.viewmodel.BookViewModel

/**
 * Created by louis383 on 2017/12/3.
 */

class BookResultAdapter(hideTitleBar: Boolean, clickHandler: BookResultClickHandler) : RecyclerView.Adapter<BookResultViewHolder>() {

    private var books: List<Book>? = null
    private var hideTitleBar: Boolean = false
    var bookResultClickHandler: BookResultClickHandler

    init {
        books = ArrayList()
        this.hideTitleBar = hideTitleBar
        this.bookResultClickHandler = clickHandler
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookResultViewHolder? {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.book_card_view, parent,false)
        return BookResultViewHolder(view, hideTitleBar)
    }

    override fun onBindViewHolder(holder: BookResultViewHolder, position: Int) {
        val index: Int = holder.adapterPosition
        if (books!!.isNotEmpty() && index < books!!.size) {
            val book: Book = books!![index]
            val bookViewModel = BookViewModel(book)
            holder.bookTitle.text = bookViewModel.getTitle()
            holder.bookDescription.text = bookViewModel.getDescription()
            holder.bookPrice.text = bookViewModel.getPrice()
            holder.bookImage.setImageURI(bookViewModel.getImage())
            holder.bookResultBody.setOnClickListener({ bookResultClickHandler.onBookCardClicked(book) })
        }
    }

    override fun getItemCount(): Int {
        return books!!.size
    }

    fun setBooks(books: List<Book>) {
        this.books = books
        notifyDataSetChanged()
    }

    fun resetBooks() {
        this.books = ArrayList()
        notifyDataSetChanged()
    }

    class BookResultViewHolder(itemView: View, hideTitleBar: Boolean) : RecyclerView.ViewHolder(itemView) {
        internal val bookShopName: TextView = itemView.findViewById(R.id.book_card_shop_name)
        internal val bookTitle: TextView = itemView.findViewById(R.id.book_card_title)
        internal val bookDescription: TextView = itemView.findViewById(R.id.book_card_description)
        internal val bookPrice: TextView = itemView.findViewById(R.id.book_card_price)
        internal val moreIcon: ImageView = itemView.findViewById(R.id.book_card_more_icon)
        internal val bookImage: SimpleDraweeView = itemView.findViewById(R.id.book_card_image)
        internal val bookResultBody: View = itemView.findViewById(R.id.book_card_item_body)

        init {
            if (hideTitleBar) {
                bookShopName.visibility = View.GONE
                moreIcon.visibility = View.GONE
            }
        }
    }
}
