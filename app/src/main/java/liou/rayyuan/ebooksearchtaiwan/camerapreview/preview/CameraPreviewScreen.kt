package liou.rayyuan.ebooksearchtaiwan.camerapreview.preview

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import android.os.Build
import android.util.Rational
import android.widget.Toast
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.surface.ImplementationMode
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.camerapreview.model.BarcodeResult
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.composables.DeviceOrientation
import liou.rayyuan.ebooksearchtaiwan.ui.composables.DeviceOrientationListener
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
    val barcodeResult by viewModel.barcode.collectAsStateWithLifecycle()
    val isbn by viewModel.isbn.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    LifecycleStartEffect(Unit) {
        viewModel.startCamera(lifecycleOwner)
        onStopOrDispose {
            viewModel.stopCamera()
            viewModel.releaseCamera()
        }
    }

    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        viewModel.errorMessage.collectLatest { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    CameraPreviewScreenContent(
        surfaceRequest = surfaceRequest,
        barcodeResult = barcodeResult,
        isbn = isbn,
        modifier = modifier,
        onOrientationChange = { orientationValue: Int ->
            viewModel.updateTargetOrientation(orientationValue)
        },
        onTapToFocus = viewModel::tapToFocus,
        onRequestWindowColorMode = onRequestWindowColorMode,
        onBarcodeAvailable = onBarcodeAvailable
    )
}

@Composable
private fun CameraPreviewScreenContent(
    surfaceRequest: SurfaceRequest?,
    barcodeResult: BarcodeResult?,
    isbn: String?,
    modifier: Modifier = Modifier,
    onOrientationChange: (orientationValue: Int) -> Unit = {},
    onTapToFocus: (x: Float, y: Float) -> Unit = { _, _ -> },
    onRequestWindowColorMode: (colorMode: Int) -> Unit = {},
    onBarcodeAvailable: (barcode: String) -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .fillMaxSize()
    ) {
        CameraPreviewView(
            surfaceRequest = surfaceRequest,
            onRequestWindowColorMode = onRequestWindowColorMode,
            onTapToFocus = onTapToFocus,
            onOrientationChange = onOrientationChange
        ) { viewWidth, viewHeight ->
            val boundingBox = barcodeResult?.boundingBox?.toComposeRect()
            if (boundingBox != null && barcodeResult.isBarcodeAvailable()) {
                val scaleFactorX = viewWidth / barcodeResult.imageHeight.toFloat()
                val scaleFactorY = viewHeight / barcodeResult.imageWidth.toFloat()

                val topLeft =
                    Offset(
                        x = boundingBox.topLeft.x * scaleFactorX,
                        y = boundingBox.topLeft.y * scaleFactorY
                    )

                val size =
                    Size(
                        width = boundingBox.size.width * scaleFactorX,
                        height = boundingBox.size.height * scaleFactorY
                    )
                Canvas(
                    modifier = Modifier
                ) {
                    drawRect(
                        color = Color.Red,
                        topLeft = topLeft,
                        size = size,
                        style = Stroke(width = 10f)
                    )
                }
            }
        }

        ElevatedCard(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .clickable {
                        if (!isbn.isNullOrEmpty()) {
                            onBarcodeAvailable(isbn)
                        }
                    }
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
            ) {
                var isScanFirstBarcode by remember { mutableStateOf(false) }
                if (!isbn.isNullOrEmpty()) {
                    val title = stringResource(id = R.string.search_with_result, isbn)
                    Text(
                        modifier = Modifier,
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start,
                    )
                    isScanFirstBarcode = true
                }

                if (!isScanFirstBarcode) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.scanner_default_status),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start,
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraPreviewView(
    surfaceRequest: SurfaceRequest?,
    modifier: Modifier = Modifier,
    onOrientationChange: (orientationValue: Int) -> Unit = {},
    onRequestWindowColorMode: (colorMode: Int) -> Unit = {},
    onTapToFocus: (x: Float, y: Float) -> Unit = { _, _ -> },
    overlay: @Composable (width: Float, height: Float) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    var orientation by remember { mutableStateOf(DeviceOrientation.Portrait) }
    DeviceOrientationListener(
        applicationContext = context,
        onOrientationChangeRawValue = onOrientationChange
    ) { deviceOrientation ->
        orientation = deviceOrientation
    }

    val implementationMode =
        if (Build.VERSION.SDK_INT > 24) {
            ImplementationMode.EXTERNAL
        } else {
            ImplementationMode.EMBEDDED
        }

    surfaceRequest?.let {
        BoxWithConstraints(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(Color.Black)
        ) {
            val maxAspectRatio = maxWidth / maxHeight
            val wideAspectRatio =
                if (orientation == DeviceOrientation.Portrait) {
                    Rational(9, 16).toFloat()
                } else {
                    Rational(16, 9).toFloat()
                }
            val shouldUseMaxWidth = maxAspectRatio <= wideAspectRatio
            val width = if (shouldUseMaxWidth) maxWidth else maxHeight * wideAspectRatio
            val height = if (!shouldUseMaxWidth) maxHeight else maxWidth / wideAspectRatio
            var boxSize by remember { mutableStateOf(Size.Zero) }

            Box(
                modifier =
                    Modifier
                        .width(width)
                        .height(height)
                        .clip(RoundedCornerShape(16.dp))
                        .onGloballyPositioned { layoutCoordinates ->
                            boxSize = layoutCoordinates.size.toSize()
                        }
            ) {
                CameraViewFinder(
                    surfaceRequest = it,
                    implementationMode = implementationMode,
                    onRequestWindowColorMode = onRequestWindowColorMode,
                    onTap = { x, y -> onTapToFocus(x, y) },
                    modifier = Modifier.fillMaxSize()
                )
                overlay(boxSize.width, boxSize.height)
            }
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
            barcodeResult =
                BarcodeResult(
                    barcodeValue = "123456789",
                    boundingBox = null,
                    imageWidth = 0,
                    imageHeight = 0
                ),
            isbn = "5566IloveU"
        )
    }
}
//endregion
