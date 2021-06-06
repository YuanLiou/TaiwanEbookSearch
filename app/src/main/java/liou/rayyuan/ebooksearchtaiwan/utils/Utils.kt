package liou.rayyuan.ebooksearchtaiwan.utils

import liou.rayyuan.ebooksearchtaiwan.model.data.DefaultStoreNames

object Utils {

    @JvmStatic
    fun getDefaultSort(): List<DefaultStoreNames> {
        return listOf(
            DefaultStoreNames.READMOO,
                DefaultStoreNames.KINDLE,
                DefaultStoreNames.KOBO,
                DefaultStoreNames.BOOK_WALKER,
                DefaultStoreNames.BOOK_COMPANY,
                DefaultStoreNames.TAAZE,
                DefaultStoreNames.PLAY_STORE,
                DefaultStoreNames.PUBU,
                DefaultStoreNames.HYREAD)
    }
}