package liou.rayyuan.ebooksearchtaiwan.domain

import android.content.Context
import androidx.preference.PreferenceManager
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import com.rayliu.commonmain.domain.service.UserPreferenceManager.Companion.KEY_SORT_BY_PRICE
import com.rayliu.commonmain.domain.service.UserPreferenceManager.Companion.KEY_USER_SYSTEM_THEME
import com.rayliu.commonmain.domain.service.UserPreferenceManager.Companion.KEY_USER_THEME
import com.rayliu.commonmain.domain.service.UserPreferenceManager.Companion.KEY_USE_CHROME_CUSTOM_VIEW
import com.rayliu.commonmain.domain.service.UserPreferenceManager.Companion.VALUE_DARK_THEME
import com.rayliu.commonmain.domain.service.UserPreferenceManager.Companion.VALUE_LIGHT_THEME

/**
 * Created by louis383 on 2018/9/29.
 */
class UserPreferenceManagerImpl(
    context: Context
) : UserPreferenceManager {
    val defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun isFollowSystemTheme(): Boolean = defaultPreferences.getBoolean(KEY_USER_SYSTEM_THEME, false)

    override fun isDarkTheme(): Boolean = defaultPreferences.getString(KEY_USER_THEME, VALUE_LIGHT_THEME) == VALUE_DARK_THEME

    override fun isPreferCustomTab(): Boolean = defaultPreferences.getBoolean(KEY_USE_CHROME_CUSTOM_VIEW, true)

    override fun isSearchResultSortByPrice(): Boolean = defaultPreferences.getBoolean(KEY_SORT_BY_PRICE, true)
}
