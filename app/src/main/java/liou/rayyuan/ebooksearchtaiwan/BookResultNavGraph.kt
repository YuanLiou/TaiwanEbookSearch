package liou.rayyuan.ebooksearchtaiwan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchViewModel
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.ServiceStatusList
import liou.rayyuan.ebooksearchtaiwan.navigation.BookResultDestinations

fun NavGraphBuilder.bookResultNavGraph(
    viewModel: BookSearchViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    composable(
        route = BookResultDestinations.SERVICE_STATUS,
    ) { _ ->
        val bookStoreDetails =
            viewModel.bookStoreDetails
                .collectAsStateWithLifecycle()
                .value

        ServiceStatusList(
            storeDetails = bookStoreDetails,
            modifier = modifier
        )
    }
    composable(
        route = BookResultDestinations.SEARCH_RESULT,
    ) { _ ->
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            Text("Search Result Screen")
        }
    }
}
