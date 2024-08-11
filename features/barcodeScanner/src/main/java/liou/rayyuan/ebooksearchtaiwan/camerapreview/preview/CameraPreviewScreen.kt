package liou.rayyuan.ebooksearchtaiwan.camerapreview.preview

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import android.os.Build
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.surface.ImplementationMode
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import liou.rayyuan.ebooksearchtaiwan.camerapreview.R
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun CameraPreviewScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraPreviewViewModel = koinViewModel(),
    onRequestWindowColorMode: (colorMode: Int) -> Unit = {},
    onBarcodeAvailable: (barcode: String) -> Unit = {}
) {
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()
    val barcodeValue by viewModel.barcodeValue.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    LifecycleStartEffect(Unit) {
        viewModel.startCamera(lifecycleOwner)
        onStopOrDispose {
            viewModel.stopCamera()
            viewModel.releaseCamera()
        }
    }

    CameraPreviewScreenContent(
        surfaceRequest = surfaceRequest,
        barcodeValue = barcodeValue,
        modifier = modifier,
        onTapToFocus = viewModel::tapToFocus,
        onRequestWindowColorMode = onRequestWindowColorMode,
        onBarcodeAvailable = onBarcodeAvailable
    )
}

@Composable
private fun CameraPreviewScreenContent(
    surfaceRequest: SurfaceRequest?,
    barcodeValue: String?,
    modifier: Modifier = Modifier,
    onTapToFocus: (x: Float, y: Float) -> Unit = { _, _ -> },
    onRequestWindowColorMode: (colorMode: Int) -> Unit = {},
    onBarcodeAvailable: (barcode: String) -> Unit = {}
) {
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
            CameraPreviewView(
                surfaceRequest = surfaceRequest,
                onRequestWindowColorMode = onRequestWindowColorMode,
                onTapToFocus = onTapToFocus,
            )

            if (!barcodeValue.isNullOrEmpty()) {
                ElevatedCard(
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .clickable {
                                onBarcodeAvailable(barcodeValue)
                            }
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                    ) {
                        val title = stringResource(id = R.string.search_with_result, barcodeValue)
                        Text(
                            modifier = Modifier,
                            text = title,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Start,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraPreviewView(
    surfaceRequest: SurfaceRequest?,
    modifier: Modifier = Modifier,
    onRequestWindowColorMode: (colorMode: Int) -> Unit = {},
    onTapToFocus: (x: Float, y: Float) -> Unit = { _, _ -> },
) {
    val implementationMode =
        if (Build.VERSION.SDK_INT > 24) {
            ImplementationMode.EXTERNAL
        } else {
            ImplementationMode.EMBEDDED
        }

    surfaceRequest?.let {
        Box(
            modifier = modifier
        ) {
            CameraViewFinder(
                surfaceRequest = it,
                implementationMode = implementationMode,
                onRequestWindowColorMode = onRequestWindowColorMode,
                onTap = { x, y -> onTapToFocus(x, y) },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

//region Preview
@Preview(
    name = "camera preview screen",
    group = "screen",
    showBackground = true,
    showSystemUi = false,
    apiLevel = 34,
    device = MDPI_DEVICES
)
@Composable
private fun CameraPreviewScreenPreview() {
    EBookTheme {
        CameraPreviewScreenContent(
            surfaceRequest = null,
            barcodeValue = "123456789"
        )
    }
}
//endregion
