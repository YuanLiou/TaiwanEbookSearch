package liou.rayyuan.ebooksearchtaiwan.model

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import liou.rayyuan.chromecustomtabhelper.Browsers
import liou.rayyuan.ebooksearchtaiwan.utils.DefaultStoreNames

/**
 * Created by louis383 on 2018/9/29.
 */
class UserPreferenceManager(context: Context) {
    companion object {
        const val KEY_USER_THEME = "app_option_preference_appearance_theme"
        const val KEY_USE_CHROME_CUSTOM_VIEW = "app_option_preference__use_chrome_custom_view"
        const val KEY_BOOK_STORE_SORT = "key-book-store-sort"
        const val KEY_PREFER_BROWSER = "preference_custom_tab_prefer_browser"
        const val KEY_CLEAN_SEARCH_RECORD = "key-clean-up-search-record"
        const val VALUE_LIGHT_THEME = "light"
        const val VALUE_DARK_THEME = "dark"

        private val VALUE_BROWSER_NAME = mapOf(
                "chrome" to Browsers.CHROME,
                "firefox" to Browsers.FIREFOX,
                "samsung" to Browsers.SAMSUNG)

        private val KEY_REORDER_BOOKSTORES = "key-reorder-bookstores"
    }

    private val defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val preferenceName = "ebook_search_settings"
    private val sharedPreference = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)

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

    fun saveBookStoreSort(bookSorts: List<DefaultStoreNames>) {
        val settingString = bookSorts.joinToString(separator = ",") { it.defaultName }
        sharedPreference.edit {
            putString(KEY_BOOK_STORE_SORT, settingString)
        }
    }

    fun getBookStoreSort(): List<DefaultStoreNames>? {
        val settingString = sharedPreference.getString(KEY_BOOK_STORE_SORT, "") ?: ""
        if (settingString.isNotBlank()) {
            val result = settingString.split(",").mapNotNull {
                DefaultStoreNames.fromName(it)
            }
            return result
        } else {
            return null
        }
    }

}
