package liou.rayyuan.ebooksearchtaiwan.preferencesetting

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.model.UserPreferenceManager

/**
 * Created by louis383 on 2018/9/29.
 */
class PreferenceSettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    internal var callback: PerferencesChangeCallback? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
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
            else -> {}
        }
    }
    //endregion

    internal interface PerferencesChangeCallback {
        fun onThemeChanged()
    }
}