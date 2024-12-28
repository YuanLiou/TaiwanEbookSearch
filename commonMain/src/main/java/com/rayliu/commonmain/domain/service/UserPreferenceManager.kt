package com.rayliu.commonmain.domain.service

import android.content.Context
import androidx.preference.PreferenceManager

/**
 * Created by louis383 on 2018/9/29.
 */
class UserPreferenceManager(
    context: Context
) {
    companion object {
        const val KEY_USER_THEME = "app_option_preference_appearance_theme"
        const val KEY_USER_SYSTEM_THEME = "app_option_preference_follow_system_theme"
        const val KEY_USE_CHROME_CUSTOM_VIEW = "app_option_preference__use_chrome_custom_view"
        const val KEY_CLEAN_SEARCH_RECORD = "key-clean-up-search-record"
        const val KEY_SORT_BY_PRICE = "app_option_preference_sort_by_book_price"
        const val VALUE_LIGHT_THEME = "light"
        const val VALUE_DARK_THEME = "dark"
        const val USER_PREFERENCE_NAME = "ebook_search_settings"
    }

    private val defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun isFollowSystemTheme(): Boolean = defaultPreferences.getBoolean(KEY_USER_SYSTEM_THEME, false)

    fun isDarkTheme(): Boolean = defaultPreferences.getString(KEY_USER_THEME, VALUE_LIGHT_THEME) == VALUE_DARK_THEME

    fun isPreferCustomTab(): Boolean = defaultPreferences.getBoolean(KEY_USE_CHROME_CUSTOM_VIEW, true)

    fun isSearchResultSortByPrice(): Boolean = defaultPreferences.getBoolean(KEY_SORT_BY_PRICE, true)
}
