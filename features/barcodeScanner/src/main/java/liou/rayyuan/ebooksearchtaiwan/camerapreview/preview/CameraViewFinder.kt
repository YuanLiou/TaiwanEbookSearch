package liou.rayyuan.ebooksearchtaiwan.camerapreview.preview

import android.content.pm.ActivityInfo
import android.os.Build
import android.util.Log
import androidx.camera.core.DynamicRange
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.camera.viewfinder.compose.Viewfinder
import androidx.camera.core.SurfaceRequest.TransformationInfo as CXTransformationInfo
import androidx.camera.viewfinder.surface.ImplementationMode
import androidx.camera.viewfinder.surface.TransformationInfo
import androidx.camera.viewfinder.surface.ViewfinderSurfaceRequest
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

@Composable
fun CameraViewFinder(
    surfaceRequest: SurfaceRequest,
    modifier: Modifier = Modifier,
    implementationMode: ImplementationMode = ImplementationMode.EXTERNAL,
    onRequestWindowColorMode: (colorMode: Int) -> Unit = {},
    onTap: (x: Float, y: Float) -> Unit = { _, _ -> }
) {
    val currentImplementationMode by rememberUpdatedState(newValue = implementationMode)
    val currentOnRequestWindowColorMode by rememberUpdatedState(newValue = onRequestWindowColorMode)
    val viewFinderArgs by produceState<ViewfinderArgs?>(initialValue = null, surfaceRequest) {
        val viewfinderSurfaceRequest =
            ViewfinderSurfaceRequest.Builder(surfaceRequest.resolution)
                .build()

        surfaceRequest.addRequestCancellationListener(Runnable::run) {
            viewfinderSurfaceRequest.markSurfaceSafeToRelease()
        }

        launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                val surface = viewfinderSurfaceRequest.getSurface()
                surfaceRequest.provideSurface(surface, Runnable::run) {
                    viewfinderSurfaceRequest.markSurfaceSafeToRelease()
                }
            } finally {
                surfaceRequest.willNotProvideSurface()
            }
        }

        val transformationInfos = MutableStateFlow<CXTransformationInfo?>(null)
        surfaceRequest.setTransformationInfoListener(Runnable::run) {
            transformationInfos.value = it
        }

        var snapshotImplementationMode: ImplementationMode? = null

        snapshotFlow { currentImplementationMode }
            .combine(transformationInfos.filterNotNull()) { implementationMode, transformInfo ->
                Pair(implementationMode, transformInfo)
            }.takeWhile { (implementationMode, _) ->
                val shouldAbort = snapshotImplementationMode != null && implementationMode != snapshotImplementationMode
                if (shouldAbort) {
                    surfaceRequest.invalidate()
                }
                !shouldAbort
            }.collectLatest { (implementationMode, transformInfo) ->
                snapshotImplementationMode = implementationMode
                value =
                    ViewfinderArgs(
                        viewfinderSurfaceRequest = viewfinderSurfaceRequest,
                        isHdrSource = surfaceRequest.dynamicRange.encoding != DynamicRange.ENCODING_SDR,
                        implementationMode = implementationMode,
                        transformationInfo =
                            TransformationInfo(
                                sourceRotation = transformInfo.rotationDegrees,
                                cropRectLeft = transformInfo.cropRect.left,
                                cropRectTop = transformInfo.cropRect.top,
                                cropRectRight = transformInfo.cropRect.right,
                                cropRectBottom = transformInfo.cropRect.bottom,
                                shouldMirror = transformInfo.isMirroring
                            )
                    )
            }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LaunchedEffect(key1 = Unit) {
            snapshotFlow { viewFinderArgs }
                .filterNotNull()
                .map { args ->
                    if (args.isHdrSource && args.implementationMode == ImplementationMode.EXTERNAL) {
                        ActivityInfo.COLOR_MODE_HDR
                    } else {
                        ActivityInfo.COLOR_MODE_DEFAULT
                    }
                }.distinctUntilChanged()
                .onEach { currentOnRequestWindowColorMode(it) }
                .onCompletion { currentOnRequestWindowColorMode(ActivityInfo.COLOR_MODE_DEFAULT) }
                .collect()
        }
    }

    val coordinateTransformer = MutableCoordinateTransformer()
    viewFinderArgs?.let { args ->
        Viewfinder(
            surfaceRequest = args.viewfinderSurfaceRequest,
            implementationMode = args.implementationMode,
            transformationInfo = args.transformationInfo,
            modifier =
                modifier.fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            with(coordinateTransformer) {
                                val tapOffset = it.transform()
                                Log.d(TAG, "onTap: $tapOffset")
                                onTap(tapOffset.x, tapOffset.y)
                            }
                        }
                    }
        )
    }
}

private data class ViewfinderArgs(
    val viewfinderSurfaceRequest: ViewfinderSurfaceRequest,
    val isHdrSource: Boolean,
    val implementationMode: ImplementationMode,
    val transformationInfo: TransformationInfo
)

private const val TAG = "CameraViewFinder"
