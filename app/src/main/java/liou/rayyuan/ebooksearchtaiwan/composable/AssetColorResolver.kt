package liou.rayyuan.ebooksearchtaiwan.composable

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat

@Composable
fun resolveColorAttribute(
    context: Context,
    @AttrRes attrResId: Int,
    fallbackColor: Color = Color.Black
): Color {
    val typedValue = remember { TypedValue() }
    return if (context.theme.resolveAttribute(attrResId, typedValue, true)) {
        if (typedValue.resourceId != 0) {
            Color(ContextCompat.getColor(context, typedValue.resourceId))
        } else {
            fallbackColor
        }
    } else {
        fallbackColor
    }
}
