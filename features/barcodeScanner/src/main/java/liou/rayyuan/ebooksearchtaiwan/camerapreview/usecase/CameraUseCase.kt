package liou.rayyuan.ebooksearchtaiwan.camerapreview.usecase

import android.app.Application
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CameraXUseCase(
    private val application: Application
) : CameraUseCase {
    private lateinit var cameraProvider: ProcessCameraProvider
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)

    override suspend fun initialize() {
        cameraProvider = ProcessCameraProvider.awaitInstance(application)
    }

    override suspend fun runCamera(lifecycleOwner: LifecycleOwner) {
        val previewUseCase =
            Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider { surfaceRequest ->
                        _surfaceRequest.value = surfaceRequest
                    }
                }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, previewUseCase)
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, Log.getStackTraceString(illegalStateException))
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, Log.getStackTraceString(illegalArgumentException))
        }
    }

    override suspend fun tapToFocus(
        x: Float,
        y: Float
    ) {
    }

    override suspend fun releaseCamera() {
        cameraProvider.unbindAll()
        _surfaceRequest.value = null
    }

    override fun getSurfaceRequest(): StateFlow<SurfaceRequest?> = _surfaceRequest.asStateFlow()

    companion object {
        private const val TAG = "CameraUseCase"
    }
}

interface CameraUseCase {
    suspend fun initialize()

    suspend fun runCamera(lifecycleOwner: LifecycleOwner)

    suspend fun tapToFocus(
        x: Float,
        y: Float
    )

    suspend fun releaseCamera()

    fun getSurfaceRequest(): StateFlow<SurfaceRequest?>
}
