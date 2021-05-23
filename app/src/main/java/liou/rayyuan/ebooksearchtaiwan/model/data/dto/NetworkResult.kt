package liou.rayyuan.ebooksearchtaiwan.model.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkResult(
    @SerialName("books")
    val books: List<NetworkBook>? = null,
    @SerialName("bookstore")
    val bookstore: NetworkBookStore,
    @SerialName("error")
    val error: String? = null,
    @SerialName("isOkay")
    val isOkay: Boolean? = null,
    @SerialName("processTime")
    val processTime: Double? = null,
    @SerialName("quantity")
    val quantity: Int? = null,
    @SerialName("status")
    val status: String? = null
)