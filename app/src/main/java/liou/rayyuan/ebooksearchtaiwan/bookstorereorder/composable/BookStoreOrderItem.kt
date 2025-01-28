package liou.rayyuan.ebooksearchtaiwan.bookstorereorder.composable

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
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
    modifier: Modifier = Modifier
) {
    var checked by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .background(EBookTheme.colors.reorderListBackgroundColor)
                .fillMaxWidth()
                .height(72.dp)
                .clickable {
                    checked = !checked
                }
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it },
            colors =
                CheckboxDefaults.colors(
                    checkedColor = EBookTheme.colors.checkBoxCheckedColor,
                    uncheckedColor = EBookTheme.colors.checkBoxNormalColor
                )
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
            modifier = Modifier.padding(16.dp)
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
