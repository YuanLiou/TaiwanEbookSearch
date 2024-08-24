package liou.rayyuan.ebooksearchtaiwan.preferencesetting

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.preferencesetting.widget.MaterialListPreference
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by louis383 on 2018/9/29.
 */
class PreferenceSettingsFragment :
    PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    private val viewModel: PreferenceSettingsViewModel by viewModel()
    internal var callback: PreferencesChangeCallback? = null

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val themeChooser = findPreference(UserPreferenceManager.KEY_USER_THEME) as? ListPreference
        val followSystemTheme =
            findPreference(UserPreferenceManager.KEY_USER_SYSTEM_THEME) as? SwitchPreferenceCompat
        val preferCustomTabs =
            findPreference(UserPreferenceManager.KEY_USE_CHROME_CUSTOM_VIEW) as? SwitchPreferenceCompat
        val cleanSearchRecord =
            findPreference(UserPreferenceManager.KEY_CLEAN_SEARCH_RECORD) as? Preference
        val uninstallAllModulesPreference =
            findPreference(UserPreferenceManager.KEY_UNINSTALL_ALL_MODULES) as? Preference

        if (preferCustomTabs != null) {
            initiateCustomTabOption(preferCustomTabs)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            followSystemTheme?.run {
                isChecked = false
                isEnabled = false
                shouldDisableView = true
            }
        }
        if (followSystemTheme?.isChecked == true) {
            themeChooser?.run {
                isEnabled = false
                shouldDisableView = true
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

    private fun initiateCustomTabOption(preferCustomTabs: SwitchPreferenceCompat) {
        if (viewModel.isTabletSize) {
            with(preferCustomTabs) {
                isChecked = false
                isEnabled = false
                shouldDisableView = true
            }
        }
    }

    private fun deleteAllSearchRecords() {
        val errorHandler =
            CoroutineExceptionHandler { _, throwable ->
                Log.d("PreferenceFragment", Log.getStackTraceString(throwable))
            }

        lifecycleScope.launch(errorHandler) {
            viewModel.deleteAllSearchRecords()
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
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        callback = null
        super.onDestroy()
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (preference is ListPreference) {
            showListPreferenceDialog(preference)
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    private fun showListPreferenceDialog(preference: ListPreference) {
        val dialogFragment = MaterialListPreference()
        dialogFragment.arguments =
            bundleOf(
                "key" to preference.key
            )
        // We must call setTargetFragment in PreferenceFragment
        //  issue: https://issuetracker.google.com/issues/181793702
        dialogFragment.setTargetFragment(this, 0)
        dialogFragment.show(parentFragmentManager, "androidx.preference.PreferenceFragment.DIALOG")
    }

    //region SharedPreferences.OnSharedPreferenceChangeListener
    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences?,
        key: String?
    ) {
        when (key) {
            UserPreferenceManager.KEY_USER_SYSTEM_THEME -> {
                val themeChooser =
                    findPreference(UserPreferenceManager.KEY_USER_THEME) as? ListPreference
                val followSystemTheme =
                    findPreference(UserPreferenceManager.KEY_USER_SYSTEM_THEME) as? SwitchPreferenceCompat
                if (followSystemTheme?.isChecked == true) {
                    themeChooser?.run {
                        isEnabled = false
                        shouldDisableView = true
                    }
                } else {
                    themeChooser?.run {
                        isEnabled = true
                        shouldDisableView = false
                    }
                }
                callback?.onThemeChanged()
            }

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
