package liou.rayyuan.ebooksearchtaiwan.camerapreview.preview

import android.os.Build
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.surface.ImplementationMode
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun CameraPreviewScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraPreviewViewModel = koinViewModel(),
    onRequestWindowColorMode: (Int) -> Unit = {}
) {
    val surfaceRequest by viewModel.surfaceRequest.collectAsState()
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
        modifier = modifier,
        onTapToFocus = viewModel::tapToFocus,
        onRequestWindowColorMode = onRequestWindowColorMode
    )
}

@Composable
private fun CameraPreviewScreenContent(
    surfaceRequest: SurfaceRequest?,
    modifier: Modifier = Modifier,
    onTapToFocus: (x: Float, y: Float) -> Unit = { _, _ -> },
    onRequestWindowColorMode: (Int) -> Unit = {},
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
                onTapToFocus = onTapToFocus
            )
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
    showSystemUi = true,
    apiLevel = 34,
    device = MDPI_DEVICES
)
@Composable
private fun CameraPreviewScreenPreview() {
    EBookTheme {
        CameraPreviewScreenContent(null)
    }
}
//endregion
