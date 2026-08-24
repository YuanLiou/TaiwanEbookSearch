package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.window.core.layout.WindowSizeClass
import liou.rayyuan.ebooksearchtaiwan.booksearch.util.isWindowWidthCompact
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
class BookSearchAdaptiveNavigationTest {
    @Test
    fun compact_withCustomTabPreference_opensCustomTab() {
        assertTrue(shouldOpenCustomTab(preferCustomTab = true, isWidthCompact = true))
    }

    @Test
    fun mediumWidth_ignoresCustomTabPreference_navigatesToDetail() {
        assertFalse(shouldOpenCustomTab(preferCustomTab = true, isWidthCompact = false))
    }

    @Test
    fun compactWidth_isCompact() {
        assertTrue(isWindowWidthCompact(windowAdaptiveInfo(widthDp = 599f)))
    }

    @Test
    fun mediumAndLargerWidths_areNotCompact() {
        listOf(600f, 840f, 1200f, 1600f).forEach { widthDp ->
            assertFalse(isWindowWidthCompact(windowAdaptiveInfo(widthDp)))
        }
    }

    @Test
    fun mediumAndLargerWidths_useTwoPaneScaffoldDirective() {
        listOf(600f, 840f, 1200f, 1600f).forEach { widthDp ->
            val directive = calculateBookSearchPaneScaffoldDirective(windowAdaptiveInfo(widthDp))

            assertTrue(directive.maxHorizontalPartitions >= 2)
        }
    }

    private fun windowAdaptiveInfo(widthDp: Float): WindowAdaptiveInfo =
        WindowAdaptiveInfo(
            windowSizeClass = WindowSizeClass(widthDp = widthDp, heightDp = 800f),
            windowPosture = Posture()
        )
}
