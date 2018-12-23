package liou.rayyuan.ebooksearchtaiwan.view

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.model.EventTracker
import liou.rayyuan.ebooksearchtaiwan.model.entity.AdapterItem
import liou.rayyuan.ebooksearchtaiwan.model.entity.Book
import liou.rayyuan.ebooksearchtaiwan.model.entity.BookHeader
import liou.rayyuan.ebooksearchtaiwan.viewmodel.BookViewModel

/**
 * Created by louis383 on 2018/1/7.
 */
class FullBookStoreResultAdapter(private val clickHandler: BookResultClickHandler,
                                 private val eventTracker: EventTracker?):
        RecyclerView.Adapter<RecyclerView.ViewHolder>(),
        BookResultClickHandler {

    private val header = 1001
    private val storeTitle = 1002
    private val bookItem = 1003

    private val items = mutableListOf<AdapterItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            header -> {
                MobileAds.initialize(parent.context, BuildConfig.AD_MOB_ID)
                val headerView: View = LayoutInflater.from(parent.context).inflate(R.layout.admob_view_header, parent, false)
                AdViewHolder(headerView)
            }
            storeTitle -> {
                val storeTitleView: View = LayoutInflater.from(parent.context).inflate(R.layout.adapter_header, parent, false)
                BookStoreTitleViewHolder(storeTitleView)
            }
            else -> {
                // Default viewType is bookItem
                val bookCardView: View = LayoutInflater.from(parent.context).inflate(R.layout.book_card_view, parent, false)
                BookCardViewHolder(bookCardView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AdViewHolder -> return
            is BookStoreTitleViewHolder -> {
                val adapterPosition = (holder.adapterPosition - 1)    // minus a position for header
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val bookHeader = items[adapterPosition] as BookHeader
                    holder.bookStoreTitle.text = holder.itemView.context.getText(bookHeader.stringId)
                    if (bookHeader.isEmptyResult) {
                        holder.bookResultIsEmptyText.visibility = View.VISIBLE
                    } else {
                        holder.bookResultIsEmptyText.visibility = View.GONE
                    }
                }
            }
            is BookCardViewHolder -> {
                val index: Int = (holder.adapterPosition - 1)    // minus a position for header
                if (index < items.size && index != RecyclerView.NO_POSITION) {
                    val book = items[index] as Book
                    val bookViewModel = BookViewModel(book)

                    with(holder) {
                        setTextOnViewHolder(bookShopName, bookViewModel.getShopName(holder.itemView.context))
                        setTextOnViewHolder(bookTitle, bookViewModel.getTitle())
                        setTextOnViewHolder(bookDescription, bookViewModel.getDescription())
                        setTextOnViewHolder(bookPrice, bookViewModel.getPrice())

                        bookImage.setResizeImage(bookViewModel.getImage())
                        bookResultBody.setOnClickListener {
                            clickHandler.onBookCardClicked(book)

                            eventTracker?.generateBookRecordBundle(book.isFirstChoice, book.bookStore)?.run {
                                eventTracker.logEvent(EventTracker.OPEN_BOOK_LINK, this)
                            }
                        }

                        if (book.isFirstChoice) {
                            bookShopName.visibility = View.VISIBLE
                            moreIcon.visibility = View.INVISIBLE
                        } else {
                            bookShopName.visibility = View.GONE
                            moreIcon.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun setTextOnViewHolder(textView: AppCompatTextView, content: String) {
        textView.setTextFuture(PrecomputedTextCompat.getTextFuture(
                content, TextViewCompat.getTextMetricsParams(textView), null))
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

    override fun getItemCount(): Int {
        if (items.isEmpty()) {
            return 0
        }

        return items.size + 1    // plus a position for header
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return header
        }

        val adapterItem = items[(position - 1)]    // minus a position for header
        if (adapterItem is BookHeader) {
            return storeTitle
        } else if (adapterItem is Book) {
            return bookItem
        }
        return super.getItemViewType(position)
    }

    fun addResult(items: List<AdapterItem>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clean() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun onBookCardClicked(book: Book) {
        clickHandler.onBookCardClicked(book)
    }

    class BookStoreTitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val bookStoreTitle: TextView = itemView.findViewById(R.id.search_result_subtitle_top)
        internal val bookResultIsEmptyText: TextView = itemView.findViewById(R.id.search_result_list_empty)
    }

    class BookCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val bookShopName: AppCompatTextView = itemView.findViewById(R.id.book_card_shop_name)
        internal val bookTitle: AppCompatTextView = itemView.findViewById(R.id.book_card_title)
        internal val bookDescription: AppCompatTextView = itemView.findViewById(R.id.book_card_description)
        internal val bookPrice: AppCompatTextView = itemView.findViewById(R.id.book_card_price)
        internal val moreIcon: ImageView = itemView.findViewById(R.id.book_card_more_icon)
        internal val bookImage: SimpleDraweeView = itemView.findViewById(R.id.book_card_image)
        internal val bookResultBody: View = itemView.findViewById(R.id.book_card_item_body)
    }

    class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookResultAdView: AdView = itemView.findViewById(R.id.admob_view_header_adview)

        init {
            val adRequestBuilder = AdRequest.Builder()
            val adRequest = adRequestBuilder.build()
            bookResultAdView.loadAd(adRequest)
        }
    }
}