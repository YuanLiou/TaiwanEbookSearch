package liou.rayyuan.ebooksearchtaiwan.booksearch

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BookSearchAdaptiveNavigationTest {
    @Test
    fun compact_withCustomTabPreference_opensCustomTab() {
        assertTrue(shouldOpenCustomTab(preferCustomTab = true, isWidthCompact = true))
    }

    @Test
    fun mediumWidth_ignoresCustomTabPreference_navigatesToDetail() {
        assertFalse(shouldOpenCustomTab(preferCustomTab = true, isWidthCompact = false))
    }
}
