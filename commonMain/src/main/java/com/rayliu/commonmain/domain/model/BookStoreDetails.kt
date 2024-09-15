package com.rayliu.commonmain.domain.model

import com.rayliu.commonmain.data.DefaultStoreNames

data class BookStoreDetails(
    val isOnline: Boolean,
    val displayName: String,
    val status: String,
    val id: String,
    val url: String,
    val isEnable: Boolean
) {
    fun lookUpStoreName(): DefaultStoreNames = DefaultStoreNames.fromName(id)
}
