package liou.rayyuan.ebooksearchtaiwan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Serializable
sealed class BookResultDestinations(
    val route: String
) {
    @Serializable
    data object LoadingScreen : BookResultDestinations("loading_screen")

    @Serializable
    data object ServiceStatus : BookResultDestinations("service_status")

    @Serializable
    data object SearchResult : BookResultDestinations("search_result")
}

@Composable
fun rememberEBookAppState(navController: NavHostController = rememberNavController()) =
    remember {
        EBookAppState(navController)
    }

class EBookAppState(
    val navController: NavHostController,
) {
    fun navigateToLoadingScreen() {
        val route = BookResultDestinations.LoadingScreen.route
        navController.navigate(route)
    }

    fun navigateToServiceStatus() {
        val route = BookResultDestinations.ServiceStatus.route
        navController.navigate(route)
    }

    fun navigateToSearchResult() {
        val route = BookResultDestinations.SearchResult.route
        navController.navigate(route) {
            popUpTo(BookResultDestinations.ServiceStatus.route) {
                inclusive = false
            }
        }
    }
}
