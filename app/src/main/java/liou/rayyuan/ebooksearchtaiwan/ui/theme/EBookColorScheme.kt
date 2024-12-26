package liou.rayyuan.ebooksearchtaiwan.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class EBookColorScheme(
    val colorPrimary: Color,
    val colorPrimaryDark: Color,
    val colorOnPrimary: Color,
    val colorSurface: Color,
    val colorAccent: Color,
    val colorSecondary: Color,
    val colorPrimaryVariant: Color,
    val colorBackground: Color,
    val textColorTertiary: Color,
    val colorControlNormal: Color,
    val statusBarColor: Color,
    val headline6TextColor: Color,
    val subtitle1TextColor: Color,
    val buttonTint: Color,
    val cardBackgroundColor: Color,
    val searchBoxColor: Color,
    val dividerColor: Color,
    val editTextHintColor: Color,
    val editTextInputColor: Color,
    val reorderListBackgroundColor: Color,
    val customTabHeaderColor: Color,
    val closeButtonColor: Color
)

val LightColorScheme =
    EBookColorScheme(
        colorPrimary = blue_green_you,
        colorPrimaryDark = blue_green_dark_you,
        colorOnPrimary = darker_gray_3B,
        colorSurface = light_blue_green_you,
        colorAccent = blue_green_you,
        colorSecondary = blue_green_you,
        colorPrimaryVariant = blue_green_dark_you,
        colorBackground = light_blue_green_you,
        textColorTertiary = darker_gray_3B,
        colorControlNormal = darker_gray_3B,
        statusBarColor = light_blue_green_you,
        headline6TextColor = darker_gray_3B,
        subtitle1TextColor = darker_gray_3B,
        buttonTint = blue_green_you,
        cardBackgroundColor = pure_white,
        searchBoxColor = pure_white,
        dividerColor = blue_green_you,
        editTextHintColor = gray,
        editTextInputColor = pure_dark,
        reorderListBackgroundColor = pure_white,
        customTabHeaderColor = blue_green_dark_you,
        closeButtonColor = dark_gray
    )

val DarkColorScheme =
    EBookColorScheme(
        colorPrimary = blue_green_you,
        colorPrimaryDark = blue_green_dark_you,
        colorOnPrimary = pure_white,
        colorSurface = dark_18,
        colorAccent = blue_green_you,
        colorSecondary = blue_green_you,
        colorPrimaryVariant = blue_green_dark_you,
        colorBackground = dark_18,
        textColorTertiary = white_BD,
        colorControlNormal = gray_d0,
        statusBarColor = dark_18,
        headline6TextColor = pure_white,
        subtitle1TextColor = pure_white,
        buttonTint = pure_white,
        cardBackgroundColor = tundora,
        searchBoxColor = gray,
        dividerColor = blue_green_you,
        editTextHintColor = white_BD,
        editTextInputColor = pure_white,
        reorderListBackgroundColor = darker_gray_3B,
        customTabHeaderColor = darker_gray_28,
        closeButtonColor = google_app_white_color
    )

val LocalColorScheme =
    staticCompositionLocalOf {
        EBookColorScheme(
            colorPrimary = Color.Unspecified,
            colorPrimaryDark = Color.Unspecified,
            colorOnPrimary = Color.Unspecified,
            colorSurface = Color.Unspecified,
            colorAccent = Color.Unspecified,
            colorSecondary = Color.Unspecified,
            colorPrimaryVariant = Color.Unspecified,
            colorBackground = Color.Unspecified,
            textColorTertiary = Color.Unspecified,
            colorControlNormal = Color.Unspecified,
            statusBarColor = Color.Unspecified,
            headline6TextColor = Color.Unspecified,
            subtitle1TextColor = Color.Unspecified,
            buttonTint = Color.Unspecified,
            cardBackgroundColor = Color.Unspecified,
            searchBoxColor = Color.Unspecified,
            dividerColor = Color.Unspecified,
            editTextHintColor = Color.Unspecified,
            editTextInputColor = Color.Unspecified,
            reorderListBackgroundColor = Color.Unspecified,
            customTabHeaderColor = Color.Unspecified,
            closeButtonColor = Color.Unspecified
        )
    }
