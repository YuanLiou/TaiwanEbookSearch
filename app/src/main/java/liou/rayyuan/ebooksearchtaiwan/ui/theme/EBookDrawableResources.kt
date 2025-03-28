package liou.rayyuan.ebooksearchtaiwan.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import liou.rayyuan.ebooksearchtaiwan.R

@Immutable
data class EBookDrawableResources(
    @DrawableRes val backToTopButtonDrawable: Int,
    @DrawableRes val reorderHandlerItemDrawable: Int
)

val LightDrawableResources =
    EBookDrawableResources(
        backToTopButtonDrawable = R.drawable.material_rounded_button,
        reorderHandlerItemDrawable = R.drawable.ic_baseline_reorder_24px
    )

val DarkDrawableResources =
    EBookDrawableResources(
        backToTopButtonDrawable = R.drawable.material_rounded_button_dark_mode,
        reorderHandlerItemDrawable = R.drawable.ic_baseline_reorder_light_24px
    )

val LocalDrawableResources =
    staticCompositionLocalOf {
        EBookDrawableResources(
            backToTopButtonDrawable = 0,
            reorderHandlerItemDrawable = 0
        )
    }
