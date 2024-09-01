package liou.rayyuan.ebooksearchtaiwan.camerapreview.preview

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.camerapreview.usecase.CameraUseCase

class CameraPreviewViewModel(
    private val cameraUseCase: CameraUseCase
) : ViewModel() {
    private var runningCameraJob: Job? = null
    private val initializationDeferred =
        viewModelScope.async {
            cameraUseCase.initialize()
        }

    val surfaceRequest = cameraUseCase.getSurfaceRequest()

    val barcode = cameraUseCase.getBarcode()

    val isbn = cameraUseCase.getIsbn()

    fun startCamera(lifecycleOwner: LifecycleOwner) {
        stopCamera()
        runningCameraJob =
            viewModelScope.launch {
                initializationDeferred.await()
                cameraUseCase.runCamera(lifecycleOwner)
            }
    }

    fun stopCamera() {
        runningCameraJob?.apply {
            if (isActive) {
                cancel()
            }
        }
    }

    fun releaseCamera() {
        viewModelScope.launch {
            cameraUseCase.releaseCamera()
        }
    }

    fun tapToFocus(
        x: Float,
        y: Float
    ) {
        viewModelScope.launch {
            cameraUseCase.tapToFocus(x, y)
        }
    }
}
