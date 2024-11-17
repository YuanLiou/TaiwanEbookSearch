package liou.rayyuan.ebooksearchtaiwan.booksearch.composable

import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import liou.rayyuan.ebooksearchtaiwan.composable.debounceClick
import liou.rayyuan.ebooksearchtaiwan.composable.iconpack.BaselineFilterCenterFocus24Px
import liou.rayyuan.ebooksearchtaiwan.composable.iconpack.EBookIcons
import liou.rayyuan.ebooksearchtaiwan.composable.iconpack.SearchBlack24Dp
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@Composable
fun SearchBox(
    text: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    showCameraButton: Boolean = false,
    enableCameraButtonClick: Boolean = true,
    enableSearchButtonClick: Boolean = true,
    enableTextField: Boolean = true,
    onPressSearch: () -> Unit = {},
    onCameraButtonPress: () -> Unit = {},
    onSearchButtonPress: () -> Unit = {},
    focusAction: FocusAction = FocusAction.NEUTRAL_STATE,
    onFocusActionFinish: () -> Unit = {},
    onFocusChange: (focusState: FocusState) -> Unit = {},
    virtualKeyboardAction: VirtualKeyboardAction = VirtualKeyboardAction.NEUTRAL_STATE,
    onKeyboardActionFinish: () -> Unit = {}
) {
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(10.dp))
                .background(EBookTheme.colors.searchBoxColor)
                .padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextInputField(
            text = text,
            onTextChange = onTextChange,
            enableTextField = enableTextField,
            onPressSearch = onPressSearch,
            shape = RoundedCornerShape(10.dp),
            focusAction = focusAction,
            onFocusActionFinish = onFocusActionFinish,
            onFocusChange = onFocusChange,
            virtualKeyboardAction = virtualKeyboardAction,
            onKeyboardActionFinish = onKeyboardActionFinish,
            modifier = Modifier.weight(1f)
        )
        if (showCameraButton) {
            Image(
                imageVector = EBookIcons.BaselineFilterCenterFocus24Px,
                contentDescription = null,
                modifier =
                    Modifier
                        .padding(8.dp)
                        .debounceClick(
                            debounceInterval = CLICK_MILLISECOND_THRESHOLD,
                            enabled = enableCameraButtonClick,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = false)
                        ) { onCameraButtonPress() }
            )
        }
        Image(
            imageVector = EBookIcons.SearchBlack24Dp,
            contentDescription = null,
            modifier =
                Modifier.padding(8.dp)
                    .debounceClick(
                        debounceInterval = CLICK_MILLISECOND_THRESHOLD,
                        enabled = enableSearchButtonClick,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false)
                    ) { onSearchButtonPress() }
        )
    }
}

private const val CLICK_MILLISECOND_THRESHOLD = 2000L

//region Preview
@Preview(
    name = "Search Box",
    group = "component",
    showBackground = true,
    showSystemUi = false,
    apiLevel = 34,
    device = MDPI_DEVICES
)
@Preview(
    name = "Search Box Dark",
    group = "component",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = 34,
    device = MDPI_DEVICES
)
@Composable
private fun SearchBoxPreview() {
    EBookTheme {
        var text by remember { mutableStateOf(TextFieldValue("")) }
        SearchBox(
            text = text,
            onTextChange = { text = it },
            showCameraButton = true
        )
    }
}
//endregion
