package com.rayliu.commonmain.domain.model

import com.rayliu.commonmain.data.DefaultStoreNames

data class BookSearchSlice(
    val bestResults: List<Book>,
    val storeSections: List<StoreResultSection>
)

data class StoreResultSection(
    val store: DefaultStoreNames,
    val isOnline: Boolean,
    val isOkay: Boolean,
    val status: String,
    val books: List<Book>
)
