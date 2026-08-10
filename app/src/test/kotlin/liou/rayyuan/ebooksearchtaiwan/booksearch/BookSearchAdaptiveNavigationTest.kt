package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.window.core.layout.WindowSizeClass
import org.junit.Assert.assertEquals
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
    fun mediumWidth_usesTwoPaneScaffoldDirective() {
        val mediumWindowInfo =
            WindowAdaptiveInfo(
                windowSizeClass = WindowSizeClass.compute(dpWidth = 700f, dpHeight = 800f),
                windowPosture = Posture()
            )

        val directive = calculateBookSearchPaneScaffoldDirective(mediumWindowInfo)

        assertEquals(2, directive.maxHorizontalPartitions)
    }
}
