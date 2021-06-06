package com.rayliu.commonmain.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
*
 "bookstore": {
    "isOnline": true,
    "displayName": "Readmoo 讀墨",
    "status": "Active",
    "website": "https://readmoo.com/",
    "id": "readmoo"
}
* }
*/
@Serializable
data class NetworkBookStore(
    @SerialName("displayName")
    val displayName: String? = null,
    @SerialName("id")
    val id: String,
    @SerialName("isOnline")
    val isOnline: Boolean? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("website")
    val website: String? = null
)
