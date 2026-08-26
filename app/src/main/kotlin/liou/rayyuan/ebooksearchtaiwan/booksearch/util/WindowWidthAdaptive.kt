package liou.rayyuan.ebooksearchtaiwan.booksearch.util

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

fun isWindowWidthCompact(windowAdaptiveInfo: WindowAdaptiveInfo): Boolean =
    !windowAdaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)
