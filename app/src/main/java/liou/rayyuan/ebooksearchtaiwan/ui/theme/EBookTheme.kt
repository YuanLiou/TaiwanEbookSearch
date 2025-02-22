package liou.rayyuan.ebooksearchtaiwan.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun EBookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme =
        when (darkTheme) {
            true -> DarkColorScheme
            false -> LightColorScheme
        }

    val drawableResources =
        when (darkTheme) {
            true -> DarkDrawableResources
            false -> LightDrawableResources
        }

    val isTabletSize = isTabletSize()
    CompositionLocalProvider(
        LocalDeviceInfo provides DeviceInfo(isTabletSize),
        LocalColorScheme provides colorScheme,
        LocalDrawableResources provides drawableResources,
        LocalIndication provides ripple(),
        content = content
    )
}

object EBookTheme {
    val colors: EBookColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalColorScheme.current
    val drawables: EBookDrawableResources
        @Composable
        @ReadOnlyComposable
        get() = LocalDrawableResources.current
}

val LocalDeviceInfo =
    staticCompositionLocalOf<DeviceInfo> {
        error("No DeviceInfo provided")
    }

data class DeviceInfo(
    val isTabletSize: Boolean
)

@Composable
fun isTabletSize(): Boolean {
    val configuration = LocalConfiguration.current
    return if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        configuration.screenWidthDp > 840
    } else {
        configuration.screenWidthDp > 600
    }
}
