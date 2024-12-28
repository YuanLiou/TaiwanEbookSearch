package liou.rayyuan.ebooksearchtaiwan.booksearch.composable

import android.content.res.Configuration
import android.view.KeyEvent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTextInputField(
    text: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enableTextField: Boolean = true,
    shape: Shape = RectangleShape,
    onPressSearch: () -> Unit = {},
    focusAction: FocusAction = FocusAction.NEUTRAL_STATE,
    onFocusActionFinish: () -> Unit = {},
    onFocusChange: (focusState: FocusState) -> Unit = {},
    virtualKeyboardAction: VirtualKeyboardAction = VirtualKeyboardAction.NEUTRAL_STATE,
    onKeyboardActionFinish: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val keyboardController = LocalSoftwareKeyboardController.current

    when (focusAction) {
        FocusAction.FOCUS -> {
            focusRequester.requestFocus()
            onFocusActionFinish()
        }

        FocusAction.UNFOCUS -> {
            focusManager.clearFocus()
            onFocusActionFinish()
        }

        else -> {}
    }

    when (virtualKeyboardAction) {
        VirtualKeyboardAction.SHOW -> {
            keyboardController?.show()
            onKeyboardActionFinish()
        }

        VirtualKeyboardAction.HIDE -> {
            keyboardController?.hide()
            onKeyboardActionFinish()
        }

        else -> {}
    }

    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        maxLines = 1,
        enabled = enableTextField,
        textStyle = LocalTextStyle.current.copy(color = EBookTheme.colors.editTextInputColor),
        keyboardOptions =
            KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
        keyboardActions =
            KeyboardActions(
                onSearch = {
                    onPressSearch()
                    focusManager.clearFocus()
                }
            ),
        modifier =
            modifier
                .focusRequester(focusRequester)
                .onFocusChanged(onFocusChange)
                .onKeyEvent {
                    if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                        onPressSearch()
                        focusManager.clearFocus()
                    } else if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ESCAPE) {
                        focusManager.clearFocus()
                    }
                    false
                }
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            value = text.text,
            enabled = enableTextField,
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            interactionSource = interactionSource,
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = EBookTheme.colors.searchBoxColor,
                    unfocusedContainerColor = EBookTheme.colors.searchBoxColor,
                    disabledContainerColor = EBookTheme.colors.searchBoxColor,
                    focusedTextColor = EBookTheme.colors.editTextInputColor,
                    unfocusedTextColor = EBookTheme.colors.editTextInputColor,
                    disabledTextColor = EBookTheme.colors.editTextInputColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = EBookTheme.colors.colorPrimary
                ),
            contentPadding =
                TextFieldDefaults.contentPaddingWithoutLabel(
                    start = 12.dp,
                    top = 8.dp,
                    end = 8.dp,
                    bottom = 8.dp
                ),
            shape = shape,
            innerTextField = innerTextField,
            placeholder = {
                Text(
                    text = stringResource(R.string.search_bar_hint),
                    style =
                        TextStyle.Default.copy(
                            color = EBookTheme.colors.editTextHintColor
                        ),
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                )
            }
        )
    }
}

enum class FocusAction {
    FOCUS,
    UNFOCUS,
    NEUTRAL_STATE
}

enum class VirtualKeyboardAction {
    SHOW,
    HIDE,
    NEUTRAL_STATE
}

//region Preview
@Preview(
    name = "Basic Text Input Field",
    group = "component",
    showBackground = true,
    showSystemUi = false,
    apiLevel = 34,
    device = MDPI_DEVICES
)
@Preview(
    name = "Basic Text Input Field Dark",
    group = "component",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = 34,
    device = MDPI_DEVICES
)
@Composable
private fun BasicTextInputFieldPreview() {
    EBookTheme {
        var text by remember { mutableStateOf(TextFieldValue("")) }
        BasicTextInputField(
            text = text,
            onTextChange = { text = it }
        )
    }
}
//endregion
