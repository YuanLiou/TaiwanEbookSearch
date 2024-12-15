package liou.rayyuan.ebooksearchtaiwan.composable.iconpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import liou.rayyuan.ebooksearchtaiwan.ui.theme.pure_dark

val EBookIcons.KeyboardArrowUp24Dp: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "KeyboardArrowUp24Dp",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
        autoMirror = true
    ).apply {
        path(fill = SolidColor(pure_dark)) {
            moveTo(7.41f, 15.41f)
            lineTo(12f, 10.83f)
            lineToRelative(4.59f, 4.58f)
            lineTo(18f, 14f)
            lineToRelative(-6f, -6f)
            lineToRelative(-6f, 6f)
            close()
        }
    }.build()
}

@Preview(showBackground = true)
@Composable
private fun KeyboardArrowUp24DpPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = EBookIcons.KeyboardArrowUp24Dp, contentDescription = null)
    }
}
