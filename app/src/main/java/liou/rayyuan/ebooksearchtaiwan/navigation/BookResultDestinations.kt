package liou.rayyuan.ebooksearchtaiwan.navigation

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
