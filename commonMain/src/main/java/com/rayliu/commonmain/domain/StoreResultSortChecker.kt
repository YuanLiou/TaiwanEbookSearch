package com.rayliu.commonmain.domain

import com.rayliu.commonmain.UserPreferenceManager
import com.rayliu.commonmain.Utils
import com.rayliu.commonmain.data.DefaultStoreNames

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