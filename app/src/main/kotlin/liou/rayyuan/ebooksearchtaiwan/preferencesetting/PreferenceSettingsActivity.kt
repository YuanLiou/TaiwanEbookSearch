package liou.rayyuan.ebooksearchtaiwan.preferencesetting

import android.os.Bundle
import android.content.Intent
import androidx.activity.compose.setContent
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.bookstorereorder.BookStoreReorderActivity
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

/**
 * Created by louis383 on 2018/9/29.
 */
class PreferenceSettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EBookTheme(
                darkTheme = isDarkTheme()
            ) {
                PreferenceSettingsScreen(
                    onBackPressed = {
                        onBackPressedDispatcher.onBackPressed()
                    },
                    onRecreateRequest = {
                        recreate()
                    },
                    onClickReorderBookStore = {
                        openBookStoreReorderActivity()
                    }
                )
            }
        }
    }

    private fun openBookStoreReorderActivity() {
        startActivity(Intent(this, BookStoreReorderActivity::class.java))
    }
}
