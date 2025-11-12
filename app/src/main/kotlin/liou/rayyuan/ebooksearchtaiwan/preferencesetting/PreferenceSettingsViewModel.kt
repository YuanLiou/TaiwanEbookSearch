package liou.rayyuan.ebooksearchtaiwan.preferencesetting

import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import com.rayliu.commonmain.domain.service.UserPreferenceManager.Companion.KEY_SORT_BY_PRICE
import com.rayliu.commonmain.domain.service.UserPreferenceManager.Companion.KEY_USER_SYSTEM_THEME
import com.rayliu.commonmain.domain.service.UserPreferenceManager.Companion.KEY_USER_THEME
import com.rayliu.commonmain.domain.service.UserPreferenceManager.Companion.KEY_USE_CHROME_CUSTOM_VIEW
import com.rayliu.commonmain.domain.service.UserPreferenceManager.Companion.VALUE_DARK_THEME
import com.rayliu.commonmain.domain.service.UserPreferenceManager.Companion.VALUE_LIGHT_THEME
import com.rayliu.commonmain.domain.usecase.DeleteAllSearchRecordUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.domain.UserPreferenceManagerImpl

class PreferenceSettingsViewModel(
    private val deleteAllSearchRecord: DeleteAllSearchRecordUseCase,
    preferenceManager: UserPreferenceManager
) : ViewModel() {
    private val defaultPreferences =
        (preferenceManager as UserPreferenceManagerImpl).defaultPreferences

    val isFollowSystemTheme =
        defaultPreferences
            .booleanFlow(KEY_USER_SYSTEM_THEME, false)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = defaultPreferences.getBoolean(KEY_USER_SYSTEM_THEME, false)
            )

    val isDarkTheme =
        defaultPreferences
            .stringFlow(KEY_USER_THEME, VALUE_LIGHT_THEME)
            .map { it == VALUE_DARK_THEME }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )

    val isPreferCustomTab =
        defaultPreferences
            .booleanFlow(KEY_USE_CHROME_CUSTOM_VIEW, true)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = defaultPreferences.getBoolean(KEY_USE_CHROME_CUSTOM_VIEW, true)
            )

    val isSearchResultSortByPrice =
        defaultPreferences
            .booleanFlow(KEY_SORT_BY_PRICE, true)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = defaultPreferences.getBoolean(KEY_SORT_BY_PRICE, true)
            )

    fun deleteAllSearchRecords() {
        viewModelScope.launch {
            deleteAllSearchRecord()
        }
    }

    fun onIsFollowSystemThemeChange(enable: Boolean) {
        defaultPreferences.edit { putBoolean(KEY_USER_SYSTEM_THEME, enable) }
    }

    fun onSearchResultSortByPriceChange(enable: Boolean) {
        defaultPreferences.edit { putBoolean(KEY_SORT_BY_PRICE, enable) }
    }

    fun onIsPreferCustomTabChange(enable: Boolean) {
        defaultPreferences.edit { putBoolean(KEY_USE_CHROME_CUSTOM_VIEW, enable) }
    }

    fun onIsDarkThemeChange(isDark: Boolean) {
        val theme = if (isDark) VALUE_DARK_THEME else VALUE_LIGHT_THEME
        defaultPreferences.edit { putString(KEY_USER_THEME, theme) }
    }
}
