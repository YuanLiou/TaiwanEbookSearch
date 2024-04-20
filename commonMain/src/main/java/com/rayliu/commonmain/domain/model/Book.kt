package com.rayliu.commonmain.domain.model

import android.os.Parcelable
import com.rayliu.commonmain.data.DefaultStoreNames
import kotlinx.parcelize.Parcelize

/**
 * Created by louis383 on 2017/11/29.
 * Update Entity to API version: 20180806
 */
@Parcelize // TODO:: Remove Parcelize
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
) : Parcelable
