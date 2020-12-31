package liou.rayyuan.ebooksearchtaiwan.model.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import liou.rayyuan.ebooksearchtaiwan.model.entity.AdapterItem
import liou.rayyuan.ebooksearchtaiwan.utils.DefaultStoreNames

/**
 * Created by louis383 on 2017/11/29.
 */
// Update Entity to API version: 20180806
@Parcelize
data class Book(val thumbnail: String,
                val priceCurrency: String,
                val price: Float,
                val link: String,
                val about: String,
                val id: String,
                val title: String,
                val authors: List<String>? = listOf(),
                var bookStore: DefaultStoreNames? = null,
                var isFirstChoice: Boolean = false) : Parcelable, AdapterItem
