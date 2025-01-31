package liou.rayyuan.ebooksearchtaiwan.bookstorereorder.composable

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rayliu.commonmain.data.DefaultStoreNames
import liou.rayyuan.ebooksearchtaiwan.bookstorereorder.SortedStore
import liou.rayyuan.ebooksearchtaiwan.composable.resolveColorAttribute
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.view.getStringResource

@Composable
fun BookStoreOrderItem(
    sortedStore: SortedStore,
    modifier: Modifier = Modifier,
    showCheckBox: Boolean = true,
    enableDragging: Boolean = true,
    disableCheckBox: Boolean = false,
    onVisibilityChange: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .background(EBookTheme.colors.reorderListBackgroundColor)
                .fillMaxWidth()
                .height(72.dp)
                .clickable(enabled = !disableCheckBox) {
                    sortedStore.isEnable.value = !sortedStore.isEnable.value
                    onVisibilityChange()
                }
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        val checkBoxAlpha =
            if (!disableCheckBox) {
                if (showCheckBox) {
                    1f
                } else {
                    0f
                }
            } else {
                0.4f
            }

        Checkbox(
            enabled = !disableCheckBox,
            checked = sortedStore.isEnable.value,
            onCheckedChange = {
                sortedStore.isEnable.value = it
                onVisibilityChange()
            },
            colors =
                CheckboxDefaults.colors(
                    checkedColor = EBookTheme.colors.checkBoxCheckedColor,
                    uncheckedColor = EBookTheme.colors.checkBoxNormalColor,
                    checkmarkColor = EBookTheme.colors.checkmarkColor
                ),
            modifier = Modifier.alpha(checkBoxAlpha)
        )
        val context = LocalContext.current
        Text(
            text = stringResource(sortedStore.defaultStoreName.getStringResource()),
            style =
                TextStyle.Default.copy(
                    fontSize = 16.sp,
                    color = resolveColorAttribute(context, android.R.attr.textColor, EBookTheme.colors.subtitle1TextColor),
                ),
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f),
        )
        Image(
            painter = painterResource(id = EBookTheme.drawables.reorderHandlerItemDrawable),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier =
                Modifier
                    .padding(16.dp)
                    .alpha(if (enableDragging) 1f else 0.4f)
        )
    }
}

@Preview(
    name = "BookStoreOrderItem",
    group = "component",
    showSystemUi = false,
    device = MDPI_DEVICES
)
@Preview(
    name = "BookStoreOrderItem Dark",
    group = "component",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = MDPI_DEVICES,
)
@Composable
private fun BookStoreOrderItemPreview() {
    EBookTheme {
        BookStoreOrderItem(
            sortedStore =
                SortedStore(
                    defaultStoreName = DefaultStoreNames.BOOK_COMPANY,
                    isVisible = true
                )
        )
    }
}
