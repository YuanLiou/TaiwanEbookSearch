package com.rayliu.commonmain

import com.rayliu.commonmain.data.DefaultStoreNames

object Utils {
    fun getDefaultSort(): List<DefaultStoreNames> =
        listOf(
            DefaultStoreNames.READMOO,
            DefaultStoreNames.KINDLE,
            DefaultStoreNames.KOBO,
            DefaultStoreNames.BOOK_WALKER,
            DefaultStoreNames.BOOK_COMPANY,
            DefaultStoreNames.TAAZE,
            DefaultStoreNames.PLAY_STORE,
            DefaultStoreNames.PUBU,
            DefaultStoreNames.HYREAD
        )
}
