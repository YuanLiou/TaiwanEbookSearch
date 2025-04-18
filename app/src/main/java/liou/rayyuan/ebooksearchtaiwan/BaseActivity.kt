package liou.rayyuan.ebooksearchtaiwan

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import liou.rayyuan.ebooksearchtaiwan.misc.EventTracker
import org.koin.android.ext.android.inject

abstract class BaseActivity(
    @LayoutRes contentLayoutId: Int = 0
) : AppCompatActivity(contentLayoutId) {
    protected val userPreferenceManager: UserPreferenceManager by inject()
    protected val eventTracker: EventTracker by inject()

    private val isOpenWithDarkTheme = userPreferenceManager.isDarkTheme()
    private val isOpenWithFollowSystemTheme = userPreferenceManager.isFollowSystemTheme()

    override fun onCreate(savedInstanceState: Bundle?) {
        setupTheme()
        setupEdgeToEdge()
        super.onCreate(savedInstanceState)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setupTheme()
        setupEdgeToEdge()
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

    private fun setupTheme() {
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
    }

    private fun setupEdgeToEdge() {
        val navigationDarkScrimColor = Color.TRANSPARENT
        enableEdgeToEdge(
            statusBarStyle =
                if (isDarkTheme()) {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                },
            navigationBarStyle =
                if (isDarkTheme()) {
                    SystemBarStyle.dark(
                        ContextCompat.getColor(
                            this,
                            R.color.material_dark_a85
                        )
                    )
                } else {
                    SystemBarStyle.light(
                        ContextCompat.getColor(
                            this,
                            R.color.light_blue_green_you_a95
                        ),
                        navigationDarkScrimColor
                    )
                }
        )
    }
}
