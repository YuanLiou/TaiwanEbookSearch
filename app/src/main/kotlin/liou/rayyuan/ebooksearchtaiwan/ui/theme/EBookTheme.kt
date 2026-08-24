package liou.rayyuan.ebooksearchtaiwan.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import liou.rayyuan.ebooksearchtaiwan.utils.WindowApiCompatibility

@Composable
fun EBookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    hasCompatibleWindowInsetsRuntime: Boolean = WindowApiCompatibility.hasCompatibleWindowInsetsRuntime,
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

    CompositionLocalProvider(
        LocalColorScheme provides colorScheme,
        LocalDrawableResources provides drawableResources,
        LocalHasCompatibleWindowInsetsRuntime provides hasCompatibleWindowInsetsRuntime,
        LocalIndication provides ripple(),
    ) {
        val themeColorScheme =
            when (darkTheme) {
                true -> DarkThemeColors
                false -> LightThemeColors
            }
        MaterialTheme(
            colorScheme = themeColorScheme,
            content = content
        )
    }
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
