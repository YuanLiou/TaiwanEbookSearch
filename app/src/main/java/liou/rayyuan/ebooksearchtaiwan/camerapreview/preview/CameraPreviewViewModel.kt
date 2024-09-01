package liou.rayyuan.ebooksearchtaiwan.camerapreview.preview

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.camerapreview.usecase.CameraUseCase
import liou.rayyuan.ebooksearchtaiwan.utils.ResourceHelper

class CameraPreviewViewModel(
    private val cameraUseCase: CameraUseCase,
    private val resourceHelper: ResourceHelper
) : ViewModel() {
    private var runningCameraJob: Job? = null
    private val initializationDeferred =
        viewModelScope.async {
            cameraUseCase.initialize()
        }

    val surfaceRequest = cameraUseCase.getSurfaceRequest()

    val barcode = cameraUseCase.getBarcode()

    val isbn = cameraUseCase.getIsbn()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    fun startCamera(lifecycleOwner: LifecycleOwner) {
        stopCamera()
        runningCameraJob =
            viewModelScope.launch {
                initializationDeferred.await()
                try {
                    cameraUseCase.runCamera(lifecycleOwner)
                } catch (error: Exception) {
                    _errorMessage.value = resourceHelper.getString(R.string.camera_opening_failed)
                }
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

    fun updateTargetOrientation(orientation: Int) {
        cameraUseCase.updateTargetOrientation(orientation)
    }
}
