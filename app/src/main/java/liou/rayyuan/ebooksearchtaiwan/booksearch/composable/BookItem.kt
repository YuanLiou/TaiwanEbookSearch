package liou.rayyuan.ebooksearchtaiwan.booksearch.composable

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@Composable
fun BookItem(modifier: Modifier = Modifier) {
    // TODO: Book Item Composable
}

@Preview(
    name = "Book Item Light",
    group = "component",
    showBackground = true,
    showSystemUi = false,
    apiLevel = 34,
    device = MDPI_DEVICES
)
@Preview(
    name = "Book Item Dark",
    group = "component",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = 34,
    device = MDPI_DEVICES
)
@Composable
private fun BookItemPreview() {
    EBookTheme {
        BookItem()
    }
}
