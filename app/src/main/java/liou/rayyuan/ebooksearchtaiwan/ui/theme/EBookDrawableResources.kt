package liou.rayyuan.ebooksearchtaiwan.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import liou.rayyuan.ebooksearchtaiwan.R

@Immutable
data class EBookDrawableResources(
    @DrawableRes val backToTopButtonDrawable: Int,
    @DrawableRes val reorderHandlerItemDrawable: Int,
    @DrawableRes val popupBackground: Int,
)

val LightDrawableResources =
    EBookDrawableResources(
        backToTopButtonDrawable = R.drawable.material_rounded_button,
        reorderHandlerItemDrawable = R.drawable.ic_baseline_reorder_24px,
        popupBackground = R.drawable.menu_dropdown_background
    )

val DarkDrawableResources =
    EBookDrawableResources(
        backToTopButtonDrawable = R.drawable.material_rounded_button_dark_mode,
        reorderHandlerItemDrawable = R.drawable.ic_baseline_reorder_light_24px,
        popupBackground = R.drawable.menu_dropdown_dark_background
    )
