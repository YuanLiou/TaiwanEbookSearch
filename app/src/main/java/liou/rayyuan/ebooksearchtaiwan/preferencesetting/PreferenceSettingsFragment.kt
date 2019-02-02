package liou.rayyuan.ebooksearchtaiwan.preferencesetting

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
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
        val preferCustomTabs = findPreference(UserPreferenceManager.KEY_USE_CHROME_CUSTOM_VIEW) as SwitchPreferenceCompat
        val choosePreferBrowser = findPreference(UserPreferenceManager.KEY_PREFER_BROWSER) as ListPreference

        if (quickChecker.isTabletSize()) {
            with(preferCustomTabs) {
                isChecked = false
                isEnabled = false
                setShouldDisableView(true)
            }

            with(choosePreferBrowser) {
                isEnabled = false
                setShouldDisableView(true)
            }
        } else {
            if (preferCustomTabs.isChecked) {
                choosePreferBrowser.summary = choosePreferBrowser.entry
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
            UserPreferenceManager.KEY_USE_CHROME_CUSTOM_VIEW -> {
                val preferCustomTabs = findPreference(UserPreferenceManager.KEY_USE_CHROME_CUSTOM_VIEW) as SwitchPreferenceCompat
                val choosePreferBrowser = findPreference(UserPreferenceManager.KEY_PREFER_BROWSER) as ListPreference
                if (preferCustomTabs.isChecked) {
                    with(choosePreferBrowser) {
                        isEnabled = true
                        setShouldDisableView(false)
                        summary = entry
                    }
                } else {
                    with(choosePreferBrowser) {
                        isEnabled = false
                        setShouldDisableView(true)
                        summary = getString(R.string.preference_custom_tab_prefer_browser_summary)
                    }
                }
            }
            UserPreferenceManager.KEY_PREFER_BROWSER -> {
                val choosePreferBrowser = findPreference(UserPreferenceManager.KEY_PREFER_BROWSER) as ListPreference
                choosePreferBrowser.summary = choosePreferBrowser.entry
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