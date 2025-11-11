package liou.rayyuan.ebooksearchtaiwan.preferencesetting

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.databinding.ActivityPreferenceBinding
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.utils.ActivityViewBinding
import liou.rayyuan.ebooksearchtaiwan.utils.setupEdgeToEdge

/**
 * Created by louis383 on 2018/9/29.
 */
class PreferenceSettingsActivity :
    BaseActivity(),
    PreferenceSettingsFragment.PreferencesChangeCallback {
//    private val viewBinding: ActivityPreferenceBinding by ActivityViewBinding(
//        ActivityPreferenceBinding::bind,
//        R.id.preference_layout_rootView
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EBookTheme(
                darkTheme = isDarkTheme()
            ) {
                PreferenceSettingsScreen(
                    onBackPressed = {
                        onBackPressedDispatcher.onBackPressed()
                    }
                )
            }
        }
//        val toolbar = viewBinding.preferenceLayoutToolbar
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeButtonEnabled(true)
//
//        if (!isDarkTheme()) {
//            viewBinding.preferenceLayoutMainContent.setBackgroundColor(ContextCompat.getColor(this, R.color.pure_white))
//        }
//
//        supportFragmentManager.commit {
//            replace(
//                R.id.preference_layout_main_content,
//                PreferenceSettingsFragment().apply {
//                    callback = this@PreferenceSettingsActivity
//                }
//            )
//        }
//        setupEdgeToEdge()
    }

//    private fun setupEdgeToEdge() {
//        viewBinding.root.setupEdgeToEdge()
//    }

    override fun onDestroy() {
        super.onDestroy()
        setSupportActionBar(null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.run {
            return when (this.itemId) {
                android.R.id.home -> {
                    onBackPressedDispatcher.onBackPressed()
                    true
                }

                else -> {
                    super.onOptionsItemSelected(this)
                }
            }
        }
    }

    //region PreferenceSettingsFragment.PreferencesChangeCallback
    override fun onThemeChanged() {
        if (isThemeChanged() || isStartToFollowSystemTheme()) {
            recreate()
        }
    }
    //endregion
}
