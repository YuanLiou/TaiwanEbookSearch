package liou.rayyuan.ebooksearchtaiwan.preferencesetting

import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import kotlinx.android.synthetic.main.activity_preference.*
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.R

/**
 * Created by louis383 on 2018/9/29.
 */
class PreferenceSettingsActivity : BaseActivity(), PreferenceSettingsFragment.PreferencesChangeCallback {
    companion object {
        const val KEY_THEME_CHANGED = "theme-changed"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)

        setSupportActionBar(preference_layout_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        preference_layout_toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.pure_white))

        supportFragmentManager.commit {
            replace(R.id.preference_layout_main_content, PreferenceSettingsFragment().apply {
                callback = this@PreferenceSettingsActivity
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item?.run {
            return when (this.itemId) {
                android.R.id.home -> {
                    onBackPressed()
                    true
                }
                else -> super.onOptionsItemSelected(this)
            }
        } ?: return super.onOptionsItemSelected(item)
    }

    //region PreferenceSettingsFragment.PreferencesChangeCallback
    override fun onThemeChanged() {
        if (isThemeChanged() || isStartToFollowSystemTheme()) {
            recreate()
        }
    }
    //endregion
}