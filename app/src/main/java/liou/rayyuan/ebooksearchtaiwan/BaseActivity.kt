package liou.rayyuan.ebooksearchtaiwan

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import liou.rayyuan.ebooksearchtaiwan.model.EventTracker
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import org.koin.android.ext.android.inject

/**
 * Created by louis383 on 2018/9/29.
 */
abstract class BaseActivity(@LayoutRes contentLayoutId: Int) : AppCompatActivity(contentLayoutId) {

    protected val userPreferenceManager: UserPreferenceManager by inject()
    protected val eventTracker: EventTracker by inject()

    private val isOpenWithDarkTheme = userPreferenceManager.isDarkTheme()
    private val isOpenWithFollowSystemTheme = userPreferenceManager.isFollowSystemTheme()

    override fun onCreate(savedInstanceState: Bundle?) {
        val shouldFollowSystemTheme = userPreferenceManager.isFollowSystemTheme()
        if (shouldFollowSystemTheme) {
            if (isSystemInNightMode()) {
                setTheme(R.style.NightTheme)
            }
        } else {
            if (isDarkTheme()) {
                setTheme(R.style.NightTheme)
            }
        }

        super.onCreate(savedInstanceState)
    }

    fun isDarkTheme(): Boolean {
        val shouldFollowSystemTheme = userPreferenceManager.isFollowSystemTheme()
        return (isSystemInNightMode() && shouldFollowSystemTheme) ||
                (userPreferenceManager.isDarkTheme() && !shouldFollowSystemTheme)
    }

    protected fun isThemeChanged(): Boolean = isOpenWithDarkTheme != isDarkTheme()

    protected fun isStartToFollowSystemTheme(): Boolean {
        val shouldFollowSystemTheme = userPreferenceManager.isFollowSystemTheme()
        return shouldFollowSystemTheme != isOpenWithFollowSystemTheme
    }

    fun isSystemInNightMode(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return false
        }

        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}