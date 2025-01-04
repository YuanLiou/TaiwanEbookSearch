package liou.rayyuan.ebooksearchtaiwan.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@Composable
fun OptionMenuItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        text = {
            Text(
                text = title,
                style =
                    TextStyle.Default.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
            )
        },
        onClick = onClick,
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
        colors =
            MenuDefaults.itemColors().copy(
                textColor = EBookTheme.colors.colorOnPrimary
            ),
        modifier = modifier
    )
}

//region Previews
@Preview(
    name = "Option Menu Light",
    group = "component",
    showBackground = true,
    showSystemUi = false,
    device = MDPI_DEVICES
)
@Preview(
    name = "Option Menu Dark",
    group = "component",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = MDPI_DEVICES
)
@Composable
private fun OptionMenuItemPreview() {
    EBookTheme {
        OptionMenuItem(
            title = "Option Menu",
            onClick = {}
        )
    }
}
//endregion
