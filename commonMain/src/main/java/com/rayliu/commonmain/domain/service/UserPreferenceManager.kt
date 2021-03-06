package com.rayliu.commonmain.domain.service

import android.content.Context
import androidx.preference.PreferenceManager
import liou.rayyuan.chromecustomtabhelper.Browsers
import com.rayliu.commonmain.data.DefaultStoreNames

/**
 * Created by louis383 on 2018/9/29.
 */
class UserPreferenceManager(context: Context) {
    companion object {
        const val KEY_USER_THEME = "app_option_preference_appearance_theme"
        const val KEY_USER_SYSTEM_THEME = "app_option_preference_follow_system_theme"
        const val KEY_USE_CHROME_CUSTOM_VIEW = "app_option_preference__use_chrome_custom_view"
        const val KEY_PREFER_BROWSER = "preference_custom_tab_prefer_browser"
        const val KEY_CLEAN_SEARCH_RECORD = "key-clean-up-search-record"
        const val VALUE_LIGHT_THEME = "light"
        const val VALUE_DARK_THEME = "dark"
        const val USER_PREFERENCE_NAME = "ebook_search_settings"
        private val VALUE_BROWSER_NAME = mapOf(
                "chrome" to Browsers.CHROME,
                "firefox" to Browsers.FIREFOX,
                "samsung" to Browsers.SAMSUNG)
    }

    private val defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun isFollowSystemTheme(): Boolean {
        return defaultPreferences.getBoolean(KEY_USER_SYSTEM_THEME, false)
    }

    fun isDarkTheme(): Boolean {
        return defaultPreferences.getString(KEY_USER_THEME, VALUE_LIGHT_THEME) == VALUE_DARK_THEME
    }

    fun isPreferCustomTab(): Boolean {
        return defaultPreferences.getBoolean(KEY_USE_CHROME_CUSTOM_VIEW, true)
    }

    fun getPreferBrowser(): Browsers {
        val preferBrowser = defaultPreferences.getString(KEY_PREFER_BROWSER, "") ?: ""
        return if (preferBrowser.isNotBlank()) {
            VALUE_BROWSER_NAME[preferBrowser] ?: Browsers.CHROME
        } else {
            Browsers.CHROME
        }
    }
}
