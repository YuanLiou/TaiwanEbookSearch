package liou.rayyuan.ebooksearchtaiwan.ui.theme

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class WindowInsetsCompatibilityInstrumentedTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun unsupportedSystemOverlays_usesZeroInsets() {
        var insetValues = emptyList<Int>()

        composeRule.setContent {
            EBookTheme(hasCompatibleWindowInsetsRuntime = false) {
                val density = Density(1f)
                val insets =
                    listOf(
                        compatibleSafeDrawingWindowInsets(),
                        compatibleScaffoldWindowInsets(),
                        compatibleTopAppBarWindowInsets(),
                    )
                SideEffect {
                    insetValues =
                        insets.flatMap {
                            listOf(
                                it.getLeft(density, LayoutDirection.Ltr),
                                it.getTop(density),
                                it.getRight(density, LayoutDirection.Ltr),
                                it.getBottom(density),
                            )
                        }
                }
            }
        }

        composeRule.runOnIdle {
            assertEquals(List(12) { 0 }, insetValues)
        }
    }
}
