package liou.rayyuan.ebooksearchtaiwan.camerapreview.preview

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@Composable
fun CameraPreviewScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier =
            modifier.fillMaxSize(),
        containerColor = EBookTheme.colors.colorBackground,
        contentColor = EBookTheme.colors.colorOnPrimary
    ) { paddings ->
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .padding(paddings)
                    .fillMaxSize()
        ) {
            Text(
                modifier = Modifier,
                text = "Preview Screen",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

//region Preview
@Preview(
    name = "camera preview screen",
    group = "screen",
    showBackground = true,
    showSystemUi = true,
    apiLevel = 34,
    device = MDPI_DEVICES
)
@Composable
private fun CameraPreviewScreenPreview() {
    EBookTheme {
        CameraPreviewScreen()
    }
}
//endregion
