package liou.rayyuan.ebooksearchtaiwan.view

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.model.EventTracker
import liou.rayyuan.ebooksearchtaiwan.model.entity.Book
import liou.rayyuan.ebooksearchtaiwan.utils.DefaultStoreNames
import liou.rayyuan.ebooksearchtaiwan.view.BookResultAdapter.BookResultViewHolder
import liou.rayyuan.ebooksearchtaiwan.viewmodel.BookViewModel

/**
 * Created by louis383 on 2017/12/3.   <br/>
 *
 * @property maxDisplayNumber if pass -1, it will display as many as it has. No display limit.
 */
class BookResultAdapter(hideTitleBar: Boolean, maxDisplayNumber: Int,
                        private val defaultStoreName: DefaultStoreNames,
                        private val eventTracker: EventTracker?) : RecyclerView.Adapter<BookResultViewHolder>() {
    private val books = mutableListOf<Book>()
    private var hideTitleBar: Boolean = false
    private val maxDisplayNumber: Int

    var bookResultClickHandler: BookResultClickHandler? = null

    init {
        this.hideTitleBar = hideTitleBar
        this.maxDisplayNumber = maxDisplayNumber
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookResultViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.book_card_view, parent,false)
        return BookResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookResultViewHolder, position: Int) {
        val index: Int = holder.adapterPosition
        if (books.isNotEmpty() && index < books.size) {
            val book: Book = books[index]
            val bookViewModel = BookViewModel(book)

            with(holder) {
                setTextOnViewHolder(bookShopName, bookViewModel.getShopName(holder.itemView.context))
                setTextOnViewHolder(bookTitle, bookViewModel.getTitle())
                setTextOnViewHolder(bookDescription, bookViewModel.getDescription())
                setTextOnViewHolder(bookPrice, bookViewModel.getPrice())

                bookImage.setResizeImage(bookViewModel.getImage())
                bookResultBody.setOnClickListener {
                    bookResultClickHandler?.onBookCardClicked(book)

                    val isFromBestResult = (defaultStoreName == DefaultStoreNames.BEST_RESULT)
                    val bookStoreName = book.bookStore ?: defaultStoreName.defaultName
                    eventTracker?.generateBookRecordBundle(isFromBestResult, bookStoreName)?.run {
                        eventTracker.logEvent(EventTracker.OPEN_BOOK_LINK, this)
                    }
                }

                if (hideTitleBar) {
                    bookShopName.visibility = View.GONE
                    moreIcon.visibility = View.GONE
                } else {
                    bookShopName.visibility = View.VISIBLE
                    moreIcon.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun setTextOnViewHolder(textView: AppCompatTextView, content: String) {
        textView.setTextFuture(PrecomputedTextCompat.getTextFuture(
                content, TextViewCompat.getTextMetricsParams(textView), null))
    }

    override fun getItemCount(): Int {
        if (maxDisplayNumber > 0 && books.size > maxDisplayNumber) {
            return maxDisplayNumber
        }
        return books.size
    }

    fun setBooks(books: List<Book>) {
        val originalIndex: Int = this.books.size
        this.books.addAll(books)
        // FIXME:: DIRTY WORK
        // To remove first result(it meant to be best price result) from the list.
        if (this.books.isNotEmpty()) {
            this.books.removeAt(0)
        }
        notifyItemRangeInserted(originalIndex, books.size)
    }

    fun addBook(book: Book, defaultStoreName: DefaultStoreNames) {
        book.bookStore = defaultStoreName.defaultName
        val originalIndex: Int = this.books.size
        this.books.add(book)
        notifyItemInserted(originalIndex)
    }

    fun resetBooks() {
        this.books.clear()
        notifyDataSetChanged()
    }

    fun getBooksCount(): Int {
        return books.size
    }

    fun isBookEmpty(): Boolean {
        return books.isEmpty()
    }

    fun sortByMoney() {
        this.books.sortWith(compareBy { it.price })
    }

    private fun SimpleDraweeView.setResizeImage(url: String) {
        val uri = Uri.parse(url)
        val resizeOption = ResizeOptions(context.resources.getDimensionPixelSize(R.dimen.list_book_cover_width),
                context.resources.getDimensionPixelSize(R.dimen.list_book_cover_height))
        val imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(resizeOption)
                .build()
        val imageController = Fresco.newDraweeControllerBuilder()
                .setOldController(controller)
                .setImageRequest(imageRequest)
                .setTapToRetryEnabled(true)
                .build()
        controller = imageController
    }

    class BookResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val bookShopName: AppCompatTextView = itemView.findViewById(R.id.book_card_shop_name)
        internal val bookTitle: AppCompatTextView = itemView.findViewById(R.id.book_card_title)
        internal val bookDescription: AppCompatTextView = itemView.findViewById(R.id.book_card_description)
        internal val bookPrice: AppCompatTextView = itemView.findViewById(R.id.book_card_price)
        internal val moreIcon: ImageView = itemView.findViewById(R.id.book_card_more_icon)
        internal val bookImage: SimpleDraweeView = itemView.findViewById(R.id.book_card_image)
        internal val bookResultBody: View = itemView.findViewById(R.id.book_card_item_body)
    }
}
