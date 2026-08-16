package liou.rayyuan.ebooksearchtaiwan.appfunctions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProductLinkValidatorTest {
    @Test
    fun validHttpProductLinks_returnTrimmedUrl() {
        assertEquals(
            "https://readmoo.com/book/123",
            ProductLinkValidator.normalize("https://readmoo.com/book/123")
        )
        assertEquals(
            "https://www.kobo.com/tw/zh/ebook/abc",
            ProductLinkValidator.normalize("  https://www.kobo.com/tw/zh/ebook/abc  ")
        )
        assertEquals(
            "http://books.com.tw/products/x",
            ProductLinkValidator.normalize("http://books.com.tw/products/x")
        )
    }

    @Test
    fun blankOrInvalidLinks_returnNull() {
        listOf(
            "",
            "   ",
            "readmoo.com/book/123",
            "javascript:alert(1)",
            "file:///tmp/book",
            "content://media/1",
            "https://",
            "https:///nohost"
        ).forEach { url ->
            assertNull(url, ProductLinkValidator.normalize(url))
        }
    }
}
