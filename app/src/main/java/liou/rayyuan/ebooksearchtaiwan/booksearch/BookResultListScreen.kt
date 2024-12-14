package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import liou.rayyuan.ebooksearchtaiwan.bookResultNavGraph
import liou.rayyuan.ebooksearchtaiwan.navigation.BookResultDestinations
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@Composable
fun BookResultListScreen(
    viewModel: BookSearchViewModel,
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navHostController,
        startDestination = BookResultDestinations.SERVICE_STATUS,
        modifier = modifier.background(EBookTheme.colors.colorBackground)
    ) {
        bookResultNavGraph(
            viewModel = viewModel,
            navController = navHostController,
            modifier = modifier
        )
    }
}
