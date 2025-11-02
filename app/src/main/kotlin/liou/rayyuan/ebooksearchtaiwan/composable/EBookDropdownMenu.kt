package liou.rayyuan.ebooksearchtaiwan.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@Composable
fun EBookDropdownMenu(
    showOptionMenu: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    DropdownMenu(
        expanded = showOptionMenu,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(10.dp),
        containerColor = EBookTheme.colors.reorderListBackgroundColor,
        shadowElevation = 4.dp,
        tonalElevation = 4.dp,
        content = content
    )
}

//region Previews
@Preview(
    name = "EBook Dropdown Menu Light",
    group = "component",
    showBackground = true,
    showSystemUi = true,
    device = MDPI_DEVICES
)
@Preview(
    name = "EBook Dropdown Menu Dark",
    group = "component",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true,
    device = MDPI_DEVICES
)
@Composable
private fun EBookDropdownMenuItemPreview() {
    EBookTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            EBookDropdownMenu(
                showOptionMenu = true,
                onDismissRequest = {}
            ) {
                OptionMenuItem(
                    title = "Option 01",
                    onClick = {}
                )
                OptionMenuItem(
                    title = "Option 02",
                    onClick = {}
                )
                OptionMenuItem(
                    title = "Option 03",
                    onClick = {}
                )
            }
        }
    }
}
//endregion
