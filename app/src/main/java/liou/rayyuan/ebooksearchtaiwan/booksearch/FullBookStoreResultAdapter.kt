package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView
import com.rayliu.commonmain.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.AdBanner
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.BookHeader
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.BookSearchItem
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.AdapterItem
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.BookHeader
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.uimodel.BookUiModel

/**
 * Created by louis383 on 2018/1/7.
 */
class FullBookStoreResultAdapter(
    private var clickHandler: BookResultClickHandler?,
    private val lookupCurrentTheme: () -> Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    BookResultClickHandler {
    private val header = 1001
    private val storeTitle = 1002
    private val bookItem = 1003

    private val items = mutableListOf<AdapterItem>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder =
        when (viewType) {
            header -> {
                AdViewComposeHolder(ComposeView(parent.context), lookupCurrentTheme)
            }

            storeTitle -> {
                BookStoreTitleComposeViewHolder(ComposeView(parent.context), lookupCurrentTheme)
            }

            else -> {
                // Default viewType is bookItem
                BookCardComposeViewHolder(ComposeView(parent.context), clickHandler, lookupCurrentTheme)
            }
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is AdViewComposeHolder -> {
                holder.loadAds()
            }

            is BookStoreTitleComposeViewHolder -> {
                val adapterPosition = (holder.absoluteAdapterPosition - 1) // minus a position for header
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val bookHeader = items[adapterPosition] as BookHeader
                    holder.bindHeader(bookHeader)
                }
            }

            is BookCardComposeViewHolder -> {
                val index: Int = (holder.absoluteAdapterPosition - 1) // minus a position for header
                if (index < items.size && index != RecyclerView.NO_POSITION) {
                    val book = items[index] as BookUiModel
                    holder.bindBook(book)
                }
            }
        }
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

    private class BookStoreTitleComposeViewHolder(
        private val composeView: ComposeView,
        private val lookupCurrentTheme: () -> Boolean
    ) : RecyclerView.ViewHolder(composeView) {
        fun bindHeader(bookHeader: BookHeader) {
            composeView.setContent {
                EBookTheme(darkTheme = lookupCurrentTheme()) {
                    val subTitle = stringResource(bookHeader.stringId)
                    val siteInfo = bookHeader.siteInfo
                    var statusText = stringResource(R.string.result_nothing)
                    var showResultStatus =
                        if (siteInfo == null) {
                            bookHeader.isEmptyResult
                        } else {
                            false
                        }

                    val isSiteOnline = siteInfo?.isOnline
                    val isResultOkay = siteInfo?.isResultOkay
                    val searchResultMessage = siteInfo?.status
                    val isResultEmpty = bookHeader.isEmptyResult
                    if (!isResultEmpty && isSiteOnline == true && isResultOkay == true) {
                        showResultStatus = false
                    }

                    if (isSiteOnline == false) {
                        statusText = stringResource(R.string.error_site_is_not_online)
                        showResultStatus = true
                    } else if (isResultOkay == false) {
                        statusText = stringResource(R.string.error_result_is_failed) + "\n" + searchResultMessage
                        showResultStatus = true
                    } else if (isResultEmpty) {
                        statusText = stringResource(R.string.result_nothing)
                        showResultStatus = true
                    }

                    BookHeader(
                        subtitle = subTitle,
                        modifier = Modifier.padding(top = 24.dp),
                        showStatusText = showResultStatus,
                        statusText = statusText
                    )
                }
            }
        }
    }

    private class BookCardComposeViewHolder(
        private val composeView: ComposeView,
        private var clickHandler: BookResultClickHandler?,
        private val lookupCurrentTheme: () -> Boolean
    ) : RecyclerView.ViewHolder(composeView) {
        fun bindBook(uiModel: BookUiModel) {
            composeView.setContent {
                EBookTheme(darkTheme = lookupCurrentTheme()) {
                    BookSearchItem(
                        uiModel = uiModel,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        onBookSearchItemClick = { book ->
                            clickHandler?.onBookCardClicked(book)
                        }
                    )
                }
            }
        }
    }

    private class AdViewComposeHolder(
        private val composeView: ComposeView,
        private val lookupCurrentTheme: () -> Boolean
    ) : RecyclerView.ViewHolder(composeView) {
        fun loadAds() {
            composeView.setContent {
                EBookTheme(darkTheme = lookupCurrentTheme()) {
                    AdBanner(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}
