package liou.rayyuan.ebooksearchtaiwan.model.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/*
* {
    "id":"14100024612",
     "thumbnail":"https://media.taaze.tw/showlargeimage.html?sc=14100024612&width=162&height=255",
     "link":"https://www.taaze.tw/sing.html?pid=14100024612",
     "pricecurrency":"twd",  // 貨幣單位（3-letter ISO 4217）
     "price":210,
     "title":"被討厭的勇氣：自我啟發之父「阿德勒」的教導",
     "about":"囧星人作品，獲囧星人授權使用\n\n\n★讓人生為之一變的全新經典，終於誕生！\n★日本亞馬遜年度冠軍書，連續300天長踞暢銷榜，街頭巷尾人手一冊！\n★榮獲日本2014年商業書大賞第三 ...",
     "publisher":"究竟出版",
     "authors":[
        "岸見一郎",
        "古賀史健",
        "葉小燕"
     ],
     "translators": [
        "林俊宏"
     ]
* }
* */
// Update Entity to API version: 20180806
@Serializable
data class NetworkBook(
    @SerialName("thumbnail")
    val thumbnail: String? = null,
    @SerialName("priceCurrency")
    val priceCurrency: String? = null,
    @SerialName("price")
    val price: Float? = null,
    @SerialName("translators")
    val translators: List<String>? = null,
    @SerialName("translator")
    val translator: String? = null,
    @SerialName("link")
    val link: String? = null,
    @SerialName("about")
    val about: String? = null,
    @SerialName("publisher")
    val publisher: String? = "",
    @SerialName("id")
    val id: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("authors")
    val authors: List<String>? = null,
    @SerialName("painters")
    val painters: List<String>? = null,
    @SerialName("nonDrmPrice")
    val nonDrmPrice: Float? = null,
    @SerialName("publishDate")
    val publishDate: String? = null
)
