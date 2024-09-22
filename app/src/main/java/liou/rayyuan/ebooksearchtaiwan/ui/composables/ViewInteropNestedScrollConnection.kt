package liou.rayyuan.ebooksearchtaiwan.ui.composables

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Velocity
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.TYPE_NON_TOUCH
import androidx.core.view.ViewCompat.TYPE_TOUCH
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun rememberViewInteropNestedScrollConnection(view: View = LocalView.current): NestedScrollConnection =
    remember(view) {
        ViewInteropNestedScrollConnection(view)
    }

private class ViewInteropNestedScrollConnection(
    private val view: View
) : NestedScrollConnection {
    private val tmpArray by lazy(LazyThreadSafetyMode.NONE) { IntArray(2) }

    private val viewHelper by lazy(LazyThreadSafetyMode.NONE) {
        NestedScrollingChildHelper(view).apply {
            isNestedScrollingEnabled = true
        }
    }

    init {
        ViewCompat.setNestedScrollingEnabled(view, true)
    }

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        if (viewHelper.startNestedScroll(available.guessScrollAxis(), source.toViewType())) {
            val parentConsumed = tmpArray.apply { fill(0) }
            viewHelper.dispatchNestedPreScroll(
                available.x.ceilAwayFromZero().toInt() * -1,
                available.y.ceilAwayFromZero().toInt() * -1,
                parentConsumed,
                null,
                source.toViewType()
            )
        }
        return Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        if (viewHelper.startNestedScroll(available.guessScrollAxis(), source.toViewType())) {
            val parentConsumed = tmpArray.apply { fill(0) }
            viewHelper.dispatchNestedScroll(
                consumed.x.ceilAwayFromZero().toInt() * -1,
                consumed.y.ceilAwayFromZero().toInt() * -1,
                available.x.ceilAwayFromZero().toInt() * -1,
                available.y.ceilAwayFromZero().toInt() * -1,
                null,
                source.toViewType(),
                parentConsumed
            )
            return toOffset(parentConsumed, available)
        }
        return Offset.Zero
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val dispatched =
            viewHelper.dispatchNestedPreFling(
                available.x * -1f,
                available.y * -1f
            ) ||
                viewHelper.dispatchNestedFling(
                    available.x * -1f,
                    available.y * -1f,
                    true
                )

        if (viewHelper.hasNestedScrollingParent(TYPE_TOUCH)) {
            viewHelper.stopNestedScroll(TYPE_TOUCH)
        } else if (viewHelper.hasNestedScrollingParent(TYPE_NON_TOUCH)) {
            viewHelper.stopNestedScroll(TYPE_NON_TOUCH)
        }

        return if (dispatched) {
            available
        } else {
            Velocity.Zero
        }
    }
}

private inline fun Float.ceilAwayFromZero(): Float = if (this >= 0) ceil(this) else floor(this)

private fun toOffset(
    consumed: IntArray,
    originalOffset: Offset
): Offset {
    require(consumed.size == 2)

    val x =
        (consumed[0] * -1f).let {
            when {
                originalOffset.x >= 0 -> it.coerceAtMost(originalOffset.x)
                else -> it.coerceAtLeast(originalOffset.x)
            }
        }

    val y =
        (consumed[1] * -1f).let {
            when {
                originalOffset.y >= 0 -> it.coerceAtMost(originalOffset.y)
                else -> it.coerceAtLeast(originalOffset.y)
            }
        }
    return Offset(x, y)
}

private fun NestedScrollSource.toViewType(): Int =
    when (this) {
        NestedScrollSource.Drag -> TYPE_TOUCH
        NestedScrollSource.UserInput -> TYPE_TOUCH
        else -> TYPE_NON_TOUCH
    }

private fun Offset.guessScrollAxis(): Int {
    var axes = ViewCompat.SCROLL_AXIS_NONE
    if (x.absoluteValue >= 0.5f) {
        axes = axes or ViewCompat.SCROLL_AXIS_HORIZONTAL
    }

    if (y.absoluteValue >= 0.5f) {
        axes = axes or ViewCompat.SCROLL_AXIS_VERTICAL
    }
    return axes
}
