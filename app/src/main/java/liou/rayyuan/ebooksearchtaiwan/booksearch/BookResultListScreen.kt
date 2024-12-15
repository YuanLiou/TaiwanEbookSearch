package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.repeatOnLifecycle
import com.rayliu.commonmain.domain.model.Book
import kotlinx.coroutines.flow.collectLatest
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.utils.navigateAndClean
import liou.rayyuan.ebooksearchtaiwan.navigation.BookResultDestinations
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@Composable
fun BookResultListScreen(
    viewModel: BookSearchViewModel,
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    onBookSearchItemClick: (Book) -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.navigationEvents.collectLatest { destinations ->
                navHostController.navigateAndClean(destinations.route)
            }
        }
    }

    NavHost(
        navController = navHostController,
        startDestination = BookResultDestinations.ServiceStatus.route,
        modifier = modifier.background(EBookTheme.colors.colorBackground)
    ) {
        bookResultNavGraph(
            viewModel = viewModel,
            modifier = modifier,
            onBookSearchItemClick = onBookSearchItemClick
        )
    }
}
