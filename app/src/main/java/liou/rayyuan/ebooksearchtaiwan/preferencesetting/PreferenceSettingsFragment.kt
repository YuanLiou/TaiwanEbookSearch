package liou.rayyuan.ebooksearchtaiwan.preferencesetting

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.model.UserPreferenceManager
import liou.rayyuan.ebooksearchtaiwan.model.dao.SearchRecordDao
import liou.rayyuan.ebooksearchtaiwan.utils.QuickChecker
import org.koin.android.ext.android.inject

/**
 * Created by louis383 on 2018/9/29.
 */
class PreferenceSettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val quickChecker: QuickChecker by inject()
    private val searchRecordDao: SearchRecordDao by inject()

    internal var callback: PreferencesChangeCallback? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val themeChooser = findPreference(UserPreferenceManager.KEY_USER_THEME) as ListPreference
        val followSystemTheme = findPreference(UserPreferenceManager.KEY_USER_SYSTEM_THEME) as SwitchPreferenceCompat
        val preferCustomTabs = findPreference(UserPreferenceManager.KEY_USE_CHROME_CUSTOM_VIEW) as SwitchPreferenceCompat
        val choosePreferBrowser = findPreference(UserPreferenceManager.KEY_PREFER_BROWSER) as ListPreference
        val cleanSearchRecord = findPreference(UserPreferenceManager.KEY_CLEAN_SEARCH_RECORD) as Preference

        initiateCustomTabOption(preferCustomTabs, choosePreferBrowser)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            with(followSystemTheme) {
                isChecked = false
                isEnabled = false
                setShouldDisableView(true)
            }
        }
        if (followSystemTheme.isChecked) {
            with(themeChooser) {
                isEnabled = false
                setShouldDisableView(true)
            }
        }

        cleanSearchRecord.setOnPreferenceClickListener {
            context?.run {
                AlertDialog.Builder(this)
                        .setTitle(R.string.preference_clean_all_records)
                        .setMessage(R.string.dialog_clean_all_records)
                        .setPositiveButton(R.string.dialog_ok) { dialog, _ ->
                            deleteAllSearchRecords()
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.dialog_cancel) { dialog, _ -> dialog.dismiss() }
                        .create().show()
            }

            true
        }
    }

    private fun initiateCustomTabOption(
        preferCustomTabs: SwitchPreferenceCompat,
        choosePreferBrowser: ListPreference
    ) {
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
            } else {
                with(choosePreferBrowser) {
                    isEnabled = false
                    setShouldDisableView(true)
                    summary = getString(R.string.preference_custom_tab_prefer_browser_summary)
                }
            }
        }
    }

    private fun deleteAllSearchRecords() {
        CoroutineScope(Dispatchers.IO).launch {
            searchRecordDao.deleteAllRecords()

            withContext(Dispatchers.Main) {
                showDeleteSearchRecordsSuccessDialog()
            }
        }
    }

    private fun showDeleteSearchRecordsSuccessDialog() {
        if (isAdded && isResumed) {
            context?.run {
                AlertDialog.Builder(this)
                        .setTitle(R.string.preference_clean_all_records)
                        .setMessage(R.string.dialog_clean_all_records_cleaned)
                        .setPositiveButton(R.string.dialog_ok) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create().show()
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

    override fun onDestroy() {
        callback = null
        super.onDestroy()
    }

    //region SharedPreferences.OnSharedPreferenceChangeListener
    override fun onSharedPreferenceChanged(sharedPreference: SharedPreferences, key: String) {
        when (key) {
            UserPreferenceManager.KEY_USER_SYSTEM_THEME -> {
                val themeChooser = findPreference(UserPreferenceManager.KEY_USER_THEME) as ListPreference
                val followSystemTheme = findPreference(UserPreferenceManager.KEY_USER_SYSTEM_THEME) as SwitchPreferenceCompat
                if (followSystemTheme.isChecked) {
                    with(themeChooser) {
                        isEnabled = false
                        setShouldDisableView(true)
                    }
                } else {
                    with(themeChooser) {
                        isEnabled = true
                        setShouldDisableView(false)
                    }
                }
                callback?.onThemeChanged()
            }
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