package liou.rayyuan.ebooksearchtaiwan.camerapreview.preview

import android.content.pm.ActivityInfo
import android.view.Surface
import androidx.camera.core.DynamicRange
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.camera.viewfinder.compose.Viewfinder
import androidx.camera.viewfinder.core.ImplementationMode
import androidx.camera.viewfinder.core.TransformationInfo
import androidx.camera.viewfinder.core.ViewfinderSurfaceRequest
import androidx.compose.foundation.layout.fillMaxSize
import androidx.camera.core.SurfaceRequest.TransformationInfo as CXTransformationInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

@Composable
fun CameraViewFinder(
    surfaceRequest: SurfaceRequest,
    modifier: Modifier = Modifier,
    implementationMode: ImplementationMode = ImplementationMode.EXTERNAL,
    coordinateTransformer: MutableCoordinateTransformer? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
    onRequestWindowColorMode: (colorMode: Int) -> Unit = {}
) {
    val currentImplementationMode by rememberUpdatedState(newValue = implementationMode)
    val currentOnRequestWindowColorMode by rememberUpdatedState(newValue = onRequestWindowColorMode)
    val viewFinderArgs by produceState<ViewfinderArgs?>(initialValue = null, surfaceRequest) {
        // Cancel this produce scope in case we haven't yet produced a complete
        // ViewfinderArgs.
        surfaceRequest.addRequestCancellationListener(Runnable::run) {
            this@produceState.cancel()
        }

        // Convert the CameraX TransformationInfos callback to a stateflow
        val transformationInfos =
            MutableStateFlow<CXTransformationInfo?>(null).also { stateFlow ->
                surfaceRequest.setTransformationInfoListener(Runnable::run) {
                    stateFlow.value = it
                }
            }.asStateFlow()

        // The ImplementationMode that will be used for all TransformationInfo updates.
        // This is locked in once we have updated ViewfinderArgs and won't change until
        // this produceState block is cancelled and restarted.
        var snapshotImplementationMode: ImplementationMode? = null
        snapshotFlow { currentImplementationMode }
            .combine(transformationInfos.filterNotNull()) { implementationMode, transformInfo ->
                Pair(implementationMode, transformInfo)
            }.takeWhile { (implementationMode, _) ->
                val shouldAbort = snapshotImplementationMode != null && implementationMode != snapshotImplementationMode
                if (shouldAbort) {
                    // Abort flow and invalidate SurfaceRequest so a new SurfaceRequest will
                    // be sent.
                    surfaceRequest.invalidate()
                } else {
                    // Got the first ImplementationMode. This will be used until this
                    // produceState is cancelled.
                    snapshotImplementationMode = implementationMode
                }
                !shouldAbort
            }.collect { (implementationMode, transformInfo) ->
                value =
                    ViewfinderArgs(
                        surfaceRequest = surfaceRequest,
                        isHdrSource = surfaceRequest.dynamicRange.encoding != DynamicRange.ENCODING_SDR,
                        implementationMode = implementationMode,
                        transformationInfo =
                            TransformationInfo(
                                sourceRotation = transformInfo.rotationDegrees,
                                cropRectLeft = transformInfo.cropRect.left.toFloat(),
                                cropRectTop = transformInfo.cropRect.top.toFloat(),
                                cropRectRight = transformInfo.cropRect.right.toFloat(),
                                cropRectBottom = transformInfo.cropRect.bottom.toFloat(),
                                isSourceMirroredHorizontally = transformInfo.isMirroring,
                                isSourceMirroredVertically = false
                            )
                    )
            }
    }

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

    viewFinderArgs?.let { args ->
        val currentArgs by rememberUpdatedState(newValue = args)
        val surfaceRequestScope by produceState<SurfaceRequestScope?>(initialValue = null) {
            snapshotFlow {
                Pair(currentArgs.surfaceRequest, currentArgs.implementationMode)
            }.collectLatest { (surfaceRequest, implementationMode) ->
                if (!value.canSupport(surfaceRequest, implementationMode)) {
                    // Create a new session if the new surface request and implementation mode
                    // do not match the current session.
                    value = SurfaceRequestScope.createFrom(surfaceRequest, implementationMode)
                }

                // Send along the surface requests until one completes or a request is cancelled.
                // We want to continue to use the same Surface until it is sent to a SurfaceRequest
                // so we don't unnecessarily recreate the underlying SurfaceView or TextureView
                try {
                    value?.requestChannel?.send(surfaceRequest)
                } catch (exception: ClosedSendChannelException) {
                    // Channel was closed. The SurfaceRequest will have willNotProvideSurface()
                    // called on it by the channel's onUndeliveredElement callback.
                }
            }
        }

        surfaceRequestScope?.let { scope ->
            DisposableEffect(scope) {
                onDispose {
                    scope.complete()
                }
            }

            Viewfinder(
                surfaceRequest = scope.viewfinderSurfaceRequest,
                transformationInfo = args.transformationInfo,
                modifier = modifier.fillMaxSize(),
                coordinateTransformer = coordinateTransformer,
                alignment = alignment,
                contentScale = contentScale
            ) {
                onSurfaceSession {
                    with(scope) {
                        for (incomingSurfaceRequest in requestChannel) {
                            // If we're providing a surface, we must wait for the source to be
                            // finished with the surface before we allow the surface session to
                            // complete, so always run inside a non-cancellable context
                            withContext(NonCancellable) {
                                val result = incomingSurfaceRequest.prodvideSurfaceAndWaitForCompletion(surface)
                                when (result.resultCode) {
                                    SurfaceRequest.Result.RESULT_SURFACE_ALREADY_PROVIDED -> {
                                        // If the surface request is already fulfilled, we need to
                                        // invalidate it so that a new surface request will be produced
                                        incomingSurfaceRequest.invalidate()
                                    }
                                    else -> {
                                        // The surface is no longer in use. It can be reused for any
                                        // future requests.
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Stable
private class SurfaceRequestScope(
    val viewfinderSurfaceRequest: ViewfinderSurfaceRequest
) {
    val requestChannel =
        Channel<SurfaceRequest>(Channel.RENDEZVOUS) {
            // If a surface hasn't yet been provided, this call will succeed. Otherwise
            // it will be a no-op.
            it.willNotProvideSurface()
        }

    suspend fun SurfaceRequest.prodvideSurfaceAndWaitForCompletion(surface: Surface): SurfaceRequest.Result =
        suspendCancellableCoroutine { continuation ->
            provideSurface(surface, Runnable::run) { continuation.resume(it) }
            continuation.invokeOnCancellation {
                assert(false) {
                    "provideSurfaceAndWaitForCompletion should always be called in a " +
                        "NonCancellable context to ensure the Surface is not closed before the " +
                        "frame source has finished using it."
                }
            }
        }

    fun complete() {
        // Ensure the surface session can exit the for-loop and finish
        requestChannel.close()
    }

    fun canSupport(
        surfaceRequest: SurfaceRequest,
        implementationMode: ImplementationMode
    ): Boolean =
        viewfinderSurfaceRequest.width == surfaceRequest.resolution.width &&
            viewfinderSurfaceRequest.height == surfaceRequest.resolution.height &&
            viewfinderSurfaceRequest.implementationMode == implementationMode

    companion object {
        fun createFrom(
            surfaceRequest: SurfaceRequest,
            implementationMode: ImplementationMode
        ): SurfaceRequestScope =
            SurfaceRequestScope(
                ViewfinderSurfaceRequest(
                    width = surfaceRequest.resolution.width,
                    height = surfaceRequest.resolution.height,
                    implementationMode = implementationMode,
                    requestId = "CXSurfaceRequest-${"%x".format(surfaceRequest.hashCode())}"
                )
            )
    }
}

private fun SurfaceRequestScope?.canSupport(
    surfaceRequest: SurfaceRequest,
    implementationMode: ImplementationMode
): Boolean = this != null && canSupport(surfaceRequest, implementationMode)

@Immutable
private data class ViewfinderArgs(
    val surfaceRequest: SurfaceRequest,
    val isHdrSource: Boolean,
    val implementationMode: ImplementationMode,
    val transformationInfo: TransformationInfo
)
