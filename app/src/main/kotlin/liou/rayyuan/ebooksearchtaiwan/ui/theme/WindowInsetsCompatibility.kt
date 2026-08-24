package liou.rayyuan.ebooksearchtaiwan.ui.theme

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import liou.rayyuan.ebooksearchtaiwan.utils.WindowApiCompatibility

internal val LocalHasCompatibleWindowInsetsRuntime =
    staticCompositionLocalOf { WindowApiCompatibility.hasCompatibleWindowInsetsRuntime }

@Composable
internal fun compatibleSafeDrawingWindowInsets(): WindowInsets =
    if (LocalHasCompatibleWindowInsetsRuntime.current) {
        WindowInsets.safeDrawing
    } else {
        WindowInsets(0, 0, 0, 0)
    }

@Composable
internal fun compatibleScaffoldWindowInsets(): WindowInsets =
    if (LocalHasCompatibleWindowInsetsRuntime.current) {
        ScaffoldDefaults.contentWindowInsets
    } else {
        WindowInsets(0, 0, 0, 0)
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun compatibleTopAppBarWindowInsets(): WindowInsets =
    if (LocalHasCompatibleWindowInsetsRuntime.current) {
        TopAppBarDefaults.windowInsets
    } else {
        WindowInsets(0, 0, 0, 0)
    }
