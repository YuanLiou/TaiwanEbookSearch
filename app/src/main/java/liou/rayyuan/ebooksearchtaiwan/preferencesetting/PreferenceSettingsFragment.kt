package liou.rayyuan.ebooksearchtaiwan.preferencesetting

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*
import liou.rayyuan.ebooksearchtaiwan.R
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import com.rayliu.commonmain.data.dao.SearchRecordDao
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
        val themeChooser = findPreference(UserPreferenceManager.KEY_USER_THEME) as? ListPreference
        val followSystemTheme = findPreference(UserPreferenceManager.KEY_USER_SYSTEM_THEME) as? SwitchPreferenceCompat
        val preferCustomTabs = findPreference(UserPreferenceManager.KEY_USE_CHROME_CUSTOM_VIEW) as? SwitchPreferenceCompat
        val choosePreferBrowser = findPreference(UserPreferenceManager.KEY_PREFER_BROWSER) as? ListPreference
        val cleanSearchRecord = findPreference(UserPreferenceManager.KEY_CLEAN_SEARCH_RECORD) as? Preference

        if (preferCustomTabs != null && choosePreferBrowser != null) {
            initiateCustomTabOption(preferCustomTabs, choosePreferBrowser)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            followSystemTheme?.run {
                isChecked = false
                isEnabled = false
                setShouldDisableView(true)
            }
        }
        if (followSystemTheme?.isChecked == true) {
            themeChooser?.run {
                isEnabled = false
                setShouldDisableView(true)
            }
        }

        cleanSearchRecord?.setOnPreferenceClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.preference_clean_all_records)
                .setMessage(R.string.dialog_clean_all_records)
                .setPositiveButton(R.string.dialog_ok) { dialog, _ ->
                    deleteAllSearchRecords()
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.dialog_cancel) { dialog, _ -> dialog.dismiss() }
                .create().show()

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
        val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.d("PreferenceFragment", Log.getStackTraceString(throwable))
        }

        lifecycleScope.launch(errorHandler) {
            withContext(Dispatchers.IO) {
                searchRecordDao.deleteAllRecords()
            }
            showDeleteSearchRecordsSuccessDialog()
        }
    }

    private fun showDeleteSearchRecordsSuccessDialog() {
        if (isAdded && isResumed) {
            requireContext().run {
                MaterialAlertDialogBuilder(this)
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
                val themeChooser = findPreference(UserPreferenceManager.KEY_USER_THEME) as? ListPreference
                val followSystemTheme = findPreference(UserPreferenceManager.KEY_USER_SYSTEM_THEME) as? SwitchPreferenceCompat
                if (followSystemTheme?.isChecked == true) {
                    themeChooser?.run {
                        isEnabled = false
                        setShouldDisableView(true)
                    }
                } else {
                    themeChooser?.run {
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
                val preferCustomTabs = findPreference(UserPreferenceManager.KEY_USE_CHROME_CUSTOM_VIEW) as? SwitchPreferenceCompat
                val choosePreferBrowser = findPreference(UserPreferenceManager.KEY_PREFER_BROWSER) as? ListPreference
                if (preferCustomTabs?.isChecked == true) {
                    choosePreferBrowser?.run {
                        isEnabled = true
                        setShouldDisableView(false)
                        summary = entry
                    }
                } else {
                    choosePreferBrowser?.run {
                        isEnabled = false
                        setShouldDisableView(true)
                        summary = getString(R.string.preference_custom_tab_prefer_browser_summary)
                    }
                }
            }
            UserPreferenceManager.KEY_PREFER_BROWSER -> {
                val choosePreferBrowser = findPreference(UserPreferenceManager.KEY_PREFER_BROWSER) as? ListPreference
                choosePreferBrowser?.summary = choosePreferBrowser?.entry
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