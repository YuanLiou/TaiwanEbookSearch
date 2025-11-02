package liou.rayyuan.ebooksearchtaiwan.composable

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

@Composable
inline fun Modifier.debounceClick(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    indication: Indication? = null,
    debounceInterval: Long = 1000L,
    crossinline onClick: () -> Unit
) = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    clickable(
        enabled = enabled,
        interactionSource = interactionSource,
        indication = indication
    ) {
        val currentTime = System.currentTimeMillis()
        if ((currentTime - lastClickTime) >= debounceInterval) {
            lastClickTime = currentTime
            onClick()
        }
    }
}
