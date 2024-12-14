package liou.rayyuan.ebooksearchtaiwan.booksearch.composable.utils

import androidx.navigation.NavHostController

fun NavHostController.navigateAndClean(route: String) {
    navigate(route = route) {
        popUpTo(graph.startDestinationId) { inclusive = true }
    }
    graph.setStartDestination(route)
}
