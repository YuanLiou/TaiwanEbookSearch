package liou.rayyuan.ebooksearchtaiwan.model.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
*
{
  "booksCompany": [],
  "readmoo": [],
  "kobo": [],
  "taaze": [],
  "bookWalker": [],
  "playStore": [],
  "pubu": [],
  "hyread": []
}
* }
*/
@Serializable
data class NetworkBookStores(
    @SerialName("booksCompany")
    val booksCompany: List<NetworkBook>?,
    @SerialName("readmoo")
    val readmoo: List<NetworkBook>?,
    @SerialName("kobo")
    val kobo: List<NetworkBook>?,
    @SerialName("taaze")
    val taaze: List<NetworkBook>?,
    @SerialName("bookWalker")
    val bookWalker: List<NetworkBook>?,
    @SerialName("playStore")
    val playStore: List<NetworkBook>?,
    @SerialName("pubu")
    val pubu: List<NetworkBook>?,
    @SerialName("hyread")
    val hyread: List<NetworkBook>?
)
