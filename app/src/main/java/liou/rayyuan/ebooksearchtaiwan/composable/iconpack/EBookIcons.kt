package liou.rayyuan.ebooksearchtaiwan.composable.iconpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

object EBookIcons

private val icons =
    listOf(
        EBookIcons.BaselineFilterCenterFocus24Px,
        EBookIcons.SearchBlack24Dp,
        EBookIcons.KeyboardArrowUp24Dp,
        EBookIcons.BaselineClear24Px,
        EBookIcons.BaselineCheck24Px
    )

@Preview(showBackground = true)
@Composable
private fun IconPackPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        icons.forEach {
            Image(imageVector = it, contentDescription = null)
        }
    }
}
