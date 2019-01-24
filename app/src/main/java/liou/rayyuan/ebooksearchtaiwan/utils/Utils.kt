package liou.rayyuan.ebooksearchtaiwan.utils

object Utils {

    @JvmStatic
    fun getDefaultSort(): List<DefaultStoreNames> {
        return listOf(DefaultStoreNames.READMOO,
                DefaultStoreNames.KOBO,
                DefaultStoreNames.BOOK_WALKER,
                DefaultStoreNames.BOOK_COMPANY,
                DefaultStoreNames.TAAZE,
                DefaultStoreNames.PLAY_STORE,
                DefaultStoreNames.PUBU,
                DefaultStoreNames.HYREAD)
    }

}