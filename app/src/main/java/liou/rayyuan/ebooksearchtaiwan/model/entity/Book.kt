package liou.rayyuan.ebooksearchtaiwan.model.entity

/**
 * Created by louis383 on 2017/11/29.
 */
/*
* {
    "id":"14100024612",
     "thumbnail":"https://media.taaze.tw/showlargeimage.html?sc=14100024612&width=162&height=255",
     "link":"https://www.taaze.tw/sing.html?pid=14100024612",
     "pricecurrency":"twd",
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
data class Book(val thumbnail: String = "",
                val priceCurrency: String = "",
                val price: Float = 0.0f,
                val translators: List<String>?,
                val link: String = "",
                val about: String = "",
                val publisher: String? = "",
                val id: String = "",
                val title: String = "",
                val authors: List<String>?,
                val painters: List<String>?,
                var bookStore: String?,
                val nonDrmPrice: Float = 0.0f)
