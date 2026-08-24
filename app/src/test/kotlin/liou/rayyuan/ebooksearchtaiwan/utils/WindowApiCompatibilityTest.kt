package liou.rayyuan.ebooksearchtaiwan.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WindowApiCompatibilityTest {
    @Test
    fun apiBelow34_doesNotRequireSystemOverlaysMethod() {
        assertTrue(
            isWindowInsetsRuntimeCompatible(sdkInt = 33) {
                throw AssertionError("Method lookup should not run below API 34")
            }
        )
    }

    @Test
    fun api34WithSystemOverlaysMethod_isSupported() {
        assertTrue(isWindowInsetsRuntimeCompatible(sdkInt = 34) { true })
    }

    @Test
    fun api34WithoutSystemOverlaysMethod_isNotSupported() {
        assertFalse(isWindowInsetsRuntimeCompatible(sdkInt = 34) { false })
    }

    @Test
    fun api34WithLinkageError_isNotSupported() {
        assertFalse(
            isWindowInsetsRuntimeCompatible(sdkInt = 34) {
                throw NoSuchMethodError("systemOverlays")
            }
        )
    }
}
