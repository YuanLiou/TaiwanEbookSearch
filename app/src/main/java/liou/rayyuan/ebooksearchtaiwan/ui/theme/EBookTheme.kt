package liou.rayyuan.ebooksearchtaiwan.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

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

    CompositionLocalProvider(
        LocalColorScheme provides colorScheme,
        LocalDrawableResources provides drawableResources,
        LocalIndication provides rememberRipple(),
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
