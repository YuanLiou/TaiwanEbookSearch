package liou.rayyuan.ebooksearchtaiwan.model.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCrawerResult(
    @SerialName("apiVersion")
    val apiVersion: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("keywords")
    val keywords: String? = null,
    @SerialName("processTime")
    val processTime: Double? = null,
    @SerialName("results")
    val networkResults: List<NetworkResult>? = null,
    @SerialName("searchDateTime")
    val searchDateTime: String? = null,
    @SerialName("totalQuantity")
    val totalQuantity: Int? = null,
)