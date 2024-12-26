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

val EBookIcons.BaselineClear24Px: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "BaselineClear24Px",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
        autoMirror = true
    ).apply {
        path(fill = SolidColor(pure_dark)) {
            moveTo(19f, 6.41f)
            lineTo(17.59f, 5f)
            lineTo(12f, 10.59f)
            lineTo(6.41f, 5f)
            lineTo(5f, 6.41f)
            lineTo(10.59f, 12f)
            lineTo(5f, 17.59f)
            lineTo(6.41f, 19f)
            lineTo(12f, 13.41f)
            lineTo(17.59f, 19f)
            lineTo(19f, 17.59f)
            lineTo(13.41f, 12f)
            close()
        }
    }.build()
}

@Preview(showBackground = true)
@Composable
private fun BaselineClear24PxPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = EBookIcons.BaselineClear24Px, contentDescription = null)
    }
}
