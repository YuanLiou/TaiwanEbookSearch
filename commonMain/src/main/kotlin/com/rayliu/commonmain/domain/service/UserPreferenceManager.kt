package com.rayliu.commonmain.domain.service

interface UserPreferenceManager {
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

    fun isFollowSystemTheme(): Boolean

    fun isDarkTheme(): Boolean

    fun isPreferCustomTab(): Boolean

    fun isSearchResultSortByPrice(): Boolean
}
