package com.rayliu.commonmain.domain.model

import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.parcelable.Parcelable
import com.rayliu.commonmain.parcelable.Parcelize

/**
 * Created by louis383 on 2017/11/29.
 * Update Entity to API version: 20180806
 */
@Parcelize
data class Book(
    val thumbnail: String,
    val priceCurrency: String,
    val price: Float,
    val link: String,
    val about: String,
    val id: String,
    val title: String,
    val authors: List<String>? = listOf(),
    val bookStore: DefaultStoreNames,
    var isFirstChoice: Boolean = false,
    val titleKeywordSimilarity: Int? = null
) : Parcelable {
    companion object {
        val DUMMY_BOOK =
            Book(
                thumbnail = "",
                priceCurrency = "TWD",
                price = 1020f,
                link = "https://www.google.com.tw",
                about = "Try boiling porridge flavored with sweet chili sauce, mashed up with basil leafs.",
                id = "-1",
                title = "Dummy",
                authors =
                    listOf(
                        "RekhaXu"
                    ),
                bookStore = DefaultStoreNames.KINDLE,
                isFirstChoice = false
            )
    }
}
