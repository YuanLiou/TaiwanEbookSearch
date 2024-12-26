package liou.rayyuan.ebooksearchtaiwan.booksearch.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rayliu.commonmain.domain.model.BookStoreDetails
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.ServiceStatusList

@Composable
fun ServiceListScreen(
    modifier: Modifier = Modifier,
    bookStoreDetails: ImmutableList<BookStoreDetails> = persistentListOf()
) {
    Box(
        modifier = modifier
    ) {
        ServiceStatusList(
            storeDetails = bookStoreDetails,
        )
    }
}
