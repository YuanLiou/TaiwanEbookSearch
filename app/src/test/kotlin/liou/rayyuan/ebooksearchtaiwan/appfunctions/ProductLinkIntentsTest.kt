package liou.rayyuan.ebooksearchtaiwan.appfunctions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProductLinkIntentsTest {
    @Test
    fun validUrlAndTitle_areKept() {
        val result =
            ProductLinkIntents.normalizeOrNull(
                url = " https://readmoo.com/book/123 ",
                title = "Atomic Habits"
            )

        assertEquals("https://readmoo.com/book/123", result?.url)
        assertEquals("Atomic Habits", result?.title)
    }

    @Test
    fun blankTitle_becomesNull() {
        val result =
            ProductLinkIntents.normalizeOrNull(
                url = "https://readmoo.com/book/123",
                title = "   "
            )

        assertEquals("https://readmoo.com/book/123", result?.url)
        assertNull(result?.title)
    }

    @Test
    fun invalidUrl_returnsNull() {
        assertNull(
            ProductLinkIntents.normalizeOrNull(
                url = "javascript:alert(1)",
                title = "Atomic Habits"
            )
        )
    }
}
