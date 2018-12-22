package liou.rayyuan.ebooksearchtaiwan.model

import android.content.Context
import androidx.preference.PreferenceManager

/**
 * Created by louis383 on 2018/9/29.
 */
class UserPreferenceManager(context: Context) {
    companion object {
        const val KEY_USER_THEME = "app_option_preference_appearance_theme"
        const val KEY_USE_CHROME_CUSTOM_VIEW = "app_option_preference__use_chrome_custom_view"
        const val VALUE_LIGHT_THEME = "light"
        const val VALUE_DARK_THEME = "dark"
    }

    private val defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun isDarkTheme(): Boolean {
        return defaultPreferences.getString(KEY_USER_THEME, VALUE_LIGHT_THEME) == VALUE_DARK_THEME
    }

    fun isPreferCustomTab(): Boolean {
        return defaultPreferences.getBoolean(KEY_USE_CHROME_CUSTOM_VIEW, true)
    }

}
