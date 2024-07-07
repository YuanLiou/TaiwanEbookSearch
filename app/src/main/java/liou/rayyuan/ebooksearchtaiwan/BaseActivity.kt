package liou.rayyuan.ebooksearchtaiwan

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import liou.rayyuan.ebooksearchtaiwan.model.EventTracker
import org.koin.android.ext.android.inject

abstract class BaseActivity(
    @LayoutRes contentLayoutId: Int
) : AppCompatActivity(contentLayoutId) {
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
        enableEdgeToEdge(
            statusBarStyle =
                if (isDarkTheme()) {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                }
        )
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

    private fun isSystemInNightMode(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return false
        }

        return resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}
