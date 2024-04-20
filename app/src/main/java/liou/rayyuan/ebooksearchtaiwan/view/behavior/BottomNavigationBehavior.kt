package liou.rayyuan.ebooksearchtaiwan.view.behavior

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import androidx.coordinatorlayout.widget.CoordinatorLayout
import liou.rayyuan.ebooksearchtaiwan.R

class BottomNavigationBehavior : CoordinatorLayout.Behavior<View> {
    constructor()

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean = child is ImageButton && child.id == R.id.search_view_back_to_top_button && axes == View.SCROLL_AXIS_VERTICAL

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        val oldTranslation = child.translationY
        val newTranslation = oldTranslation + dy

        val marginSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, coordinatorLayout.context.resources.displayMetrics)
        when {
            newTranslation > child.height -> child.translationY = child.height.toFloat()
            newTranslation <= -marginSize -> {
                child.translationY = -marginSize
                (child as ImageButton).isEnabled = true
            }
            else -> {
                child.translationY = newTranslation
                (child as ImageButton).isEnabled = false
            }
        }
    }
}
