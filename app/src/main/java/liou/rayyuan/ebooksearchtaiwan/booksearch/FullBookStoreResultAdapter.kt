package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.rayliu.commonmain.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.AdapterItem
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.BookHeader
import liou.rayyuan.ebooksearchtaiwan.uimodel.BookUiModel

/**
 * Created by louis383 on 2018/1/7.
 */
class FullBookStoreResultAdapter(
    private var clickHandler: BookResultClickHandler?,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    BookResultClickHandler {
    private val header = 1001
    private val storeTitle = 1002
    private val bookItem = 1003

    private val items = mutableListOf<AdapterItem>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            header -> {
                MobileAds.initialize(parent.context)
                val headerView: View =
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.admob_view_header,
                        parent,
                        false
                    )
                AdViewHolder(headerView)
            }
            storeTitle -> {
                val storeTitleView: View =
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.adapter_header,
                        parent,
                        false
                    )
                BookStoreTitleViewHolder(storeTitleView)
            }
            else -> {
                // Default viewType is bookItem
                val bookCardView: View =
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.book_card_view,
                        parent,
                        false
                    )
                BookCardViewHolder(bookCardView)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is AdViewHolder -> return
            is BookStoreTitleViewHolder -> {
                val adapterPosition = (holder.absoluteAdapterPosition - 1) // minus a position for header
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val bookHeader = items[adapterPosition] as BookHeader
                    bindHeader(holder, bookHeader)
                }
            }
            is BookCardViewHolder -> {
                val index: Int = (holder.absoluteAdapterPosition - 1) // minus a position for header
                if (index < items.size && index != RecyclerView.NO_POSITION) {
                    val book = items[index] as BookUiModel
                    bindBook(holder, book)
                }
            }
        }
    }

    private fun bindHeader(
        holder: BookStoreTitleViewHolder,
        bookHeader: BookHeader
    ) {
        holder.bookStoreTitle.text = holder.itemView.context.getText(bookHeader.stringId)
        val siteInfo = bookHeader.siteInfo
        if (siteInfo == null) {
            checkIsEmptyResult(bookHeader, holder)
            return
        }

        val isSiteOnline = siteInfo.isOnline
        val isResultOkay = siteInfo.isResultOkay
        val searchResultMessage = siteInfo.status
        val isResultEmpty = bookHeader.isEmptyResult
        if (!isResultEmpty && isSiteOnline && isResultOkay) {
            holder.bookResultStatusText.visibility = View.GONE
            return
        }

        if (!isSiteOnline) {
            holder.bookResultStatusText.text =
                holder.itemView.context.getText(
                    R.string.error_site_is_not_online
                )
            holder.bookResultStatusText.visibility = View.VISIBLE
        } else if (!isResultOkay) {
            val failedMessage = holder.itemView.context.getText(R.string.error_result_is_failed).toString() + "\n" + searchResultMessage
            holder.bookResultStatusText.text = failedMessage
            holder.bookResultStatusText.visibility = View.VISIBLE
        } else if (isResultEmpty) {
            holder.bookResultStatusText.text =
                holder.itemView.context.getText(
                    R.string.result_nothing
                )
            holder.bookResultStatusText.visibility = View.VISIBLE
        }
    }

    private fun checkIsEmptyResult(
        bookHeader: BookHeader,
        holder: BookStoreTitleViewHolder
    ) {
        if (bookHeader.isEmptyResult) {
            holder.bookResultStatusText.text =
                holder.itemView.context.getText(
                    R.string.result_nothing
                )
            holder.bookResultStatusText.visibility = View.VISIBLE
        } else {
            holder.bookResultStatusText.visibility = View.GONE
        }
    }

    private fun bindBook(
        holder: BookCardViewHolder,
        uiModel: BookUiModel
    ) {
        with(holder) {
            val shopName = uiModel.getShopName(holder.itemView.context)
            setTextOnViewHolder(bookShopName, shopName)
            setTextOnViewHolder(bookTitle, uiModel.getTitle())
            setTextOnViewHolder(bookDescription, uiModel.getDescription())
            setTextOnViewHolder(bookPrice, uiModel.getPrice())

            bookImage.load(uiModel.getImage()) {
                lifecycle(lifecycleOwner)
                crossfade(true)
                placeholder(R.drawable.book_image_placeholder)
                allowRgb565(true)
                transformations(RoundedCornersTransformation(holder.getRoundedCornerValue()))
            }

            val book = uiModel.book
            bookResultBody.setOnClickListener {
                clickHandler?.onBookCardClicked(book)
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

    private fun setTextOnViewHolder(
        textView: AppCompatTextView,
        content: String
    ) {
        textView.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                content,
                TextViewCompat.getTextMetricsParams(textView),
                null
            )
        )
    }

    override fun getItemCount(): Int {
        if (items.isEmpty()) {
            return 0
        }

        return items.size + 1 // plus a position for header
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return header
        }

        val adapterItem = items[(position - 1)] // minus a position for header
        if (adapterItem is BookHeader) {
            return storeTitle
        } else if (adapterItem is BookUiModel) {
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
        clickHandler?.onBookCardClicked(book)
    }

    fun release() {
        clickHandler = null
    }

    class BookStoreTitleViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val bookStoreTitle: TextView = itemView.findViewById(R.id.search_result_subtitle_top)
        val bookResultStatusText: TextView = itemView.findViewById(R.id.search_result_message_text)
    }

    class BookCardViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val bookShopName: AppCompatTextView = itemView.findViewById(R.id.book_card_shop_name)
        val bookTitle: AppCompatTextView = itemView.findViewById(R.id.book_card_title)
        val bookDescription: AppCompatTextView = itemView.findViewById(R.id.book_card_description)
        val bookPrice: AppCompatTextView = itemView.findViewById(R.id.book_card_price)
        val moreIcon: ImageView = itemView.findViewById(R.id.book_card_more_icon)
        val bookImage: ImageView = itemView.findViewById(R.id.book_card_image)
        val bookResultBody: View = itemView.findViewById(R.id.book_card_item_body)

        fun getRoundedCornerValue(): Float = itemView.context.resources.getDimension(R.dimen.image_round_corner)
    }

    class AdViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private val bookResultAdView: AdView = itemView.findViewById(R.id.admob_view_header_adview)

        init {
            val adRequestBuilder = AdRequest.Builder()
            val adRequest = adRequestBuilder.build()
            bookResultAdView.loadAd(adRequest)
        }
    }
}
