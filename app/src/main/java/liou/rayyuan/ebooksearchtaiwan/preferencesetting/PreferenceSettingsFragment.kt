package liou.rayyuan.ebooksearchtaiwan.preferencesetting

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.model.UserPreferenceManager
import liou.rayyuan.ebooksearchtaiwan.utils.QuickChecker
import org.koin.android.ext.android.inject

/**
 * Created by louis383 on 2018/9/29.
 */
class PreferenceSettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val quickChecker: QuickChecker by inject()
    internal var callback: PreferencesChangeCallback? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        if (quickChecker.isTabletSize()) {
            val preferCustomTabs = findPreference("app_option_preference__use_chrome_custom_view") as SwitchPreferenceCompat
            with(preferCustomTabs) {
                isChecked = false
                isEnabled = false
                setShouldDisableView(true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    //region SharedPreferences.OnSharedPreferenceChangeListener
    override fun onSharedPreferenceChanged(sharedPreference: SharedPreferences, key: String) {
        when (key) {
            UserPreferenceManager.KEY_USER_THEME -> {
                callback?.onThemeChanged()
            }
            else -> {
                Log.i("PreferenceSettings", "The value of $key changed")
            }
        }
    }
    //endregion

    internal interface PreferencesChangeCallback {
        fun onThemeChanged()
    }
}