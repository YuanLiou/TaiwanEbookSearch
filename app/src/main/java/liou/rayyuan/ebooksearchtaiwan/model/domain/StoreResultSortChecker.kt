package liou.rayyuan.ebooksearchtaiwan.model.domain

import liou.rayyuan.ebooksearchtaiwan.model.UserPreferenceManager
import liou.rayyuan.ebooksearchtaiwan.model.data.DefaultStoreNames
import liou.rayyuan.ebooksearchtaiwan.utils.Utils

class StoreResultSortChecker(
    private val preferenceManager: UserPreferenceManager
) {

    fun getDefaultSort(): List<DefaultStoreNames> {
        val userBookResultSort = preferenceManager.getBookStoreSort()
        if (userBookResultSort == null) {
            val defaultSort = Utils.getDefaultSort()
            preferenceManager.saveBookStoreSort(defaultSort)
            return defaultSort
        }
        return userBookResultSort
    }
}