package liou.rayyuan.ebooksearchtaiwan.booksearch.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchViewModel
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.ServiceStatusList

@Composable
fun ServiceListScreen(
    viewModel: BookSearchViewModel,
    modifier: Modifier = Modifier
) {
    val bookStoreDetails =
        viewModel.bookStoreDetails
            .collectAsStateWithLifecycle()
            .value

    ServiceStatusList(
        storeDetails = bookStoreDetails,
        modifier = modifier
    )
}
