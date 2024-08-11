package liou.rayyuan.ebooksearchtaiwan.camerapreview.usecase

import android.app.Application
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.MeteringPoint
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class CameraXUseCase(
    private val application: Application
) : CameraUseCase {
    private lateinit var cameraProvider: ProcessCameraProvider
    private val focusMeteringEvents =
        Channel<FocusMeteringEvent>(capacity = Channel.CONFLATED)
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    private val _barcodeValues = MutableStateFlow<String?>(null)

    override suspend fun initialize() {
        cameraProvider = ProcessCameraProvider.awaitInstance(application)
    }

    override suspend fun runCamera(lifecycleOwner: LifecycleOwner) {
        val previewUseCase = buildPreviewUseCase()
        val analysisUseCase = buildAnalysisUseCase()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, previewUseCase, analysisUseCase)
            focusMeteringEvents.consumeAsFlow().collect {
                val focusMeteringAction =
                    FocusMeteringAction.Builder(it.meteringPoint).build()
                Log.d(TAG, "Starting focus and metering")
                camera.cameraControl.startFocusAndMetering(focusMeteringAction)
            }
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, Log.getStackTraceString(illegalStateException))
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, Log.getStackTraceString(illegalArgumentException))
        }
    }

    private fun buildPreviewUseCase(): Preview {
        val previewUseCase =
            Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider { surfaceRequest ->
                        _surfaceRequest.value = surfaceRequest
                    }
                }
        return previewUseCase
    }

    private fun buildAnalysisUseCase(): ImageAnalysis {
        val options =
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_EAN_13)
                .build()
        val scanner = BarcodeScanning.getClient(options)
        val analysisUseCase = ImageAnalysis.Builder().build()
        analysisUseCase.setAnalyzer(
            Executors.newSingleThreadExecutor()
        ) { imageProxy ->
            processImageProxy(imageProxy, scanner)
        }
        return analysisUseCase
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(
        imageProxy: ImageProxy,
        scanner: BarcodeScanner
    ) {
        imageProxy.image?.let { currentImage ->
            val inputImage = InputImage.fromMediaImage(currentImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    val barcodeValue = barcodes.firstOrNull()?.rawValue
                    if (!barcodeValue.isNullOrEmpty()) {
                        _barcodeValues.value = barcodeValue
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, Log.getStackTraceString(exception))
                }
                .addOnCompleteListener {
                    currentImage.close()
                    imageProxy.close()
                }
        }
    }

    override suspend fun tapToFocus(
        x: Float,
        y: Float
    ) {
        getSurfaceRequest().filterNotNull().map { surfaceRequest ->
            SurfaceOrientedMeteringPointFactory(
                surfaceRequest.resolution.width.toFloat(),
                surfaceRequest.resolution.height.toFloat()
            )
        }.collectLatest { factory ->
            val meteringPoint = factory.createPoint(x, y)
            focusMeteringEvents.send(FocusMeteringEvent(meteringPoint))
        }
    }

    override suspend fun releaseCamera() {
        cameraProvider.unbindAll()
        _surfaceRequest.value = null
    }

    override fun getSurfaceRequest(): StateFlow<SurfaceRequest?> = _surfaceRequest.asStateFlow()

    override fun getBarcodeValue(): StateFlow<String?> = _barcodeValues.asStateFlow()

    companion object {
        private const val TAG = "CameraUseCase"
    }
}

data class FocusMeteringEvent(
    val meteringPoint: MeteringPoint
)

interface CameraUseCase {
    suspend fun initialize()

    suspend fun runCamera(lifecycleOwner: LifecycleOwner)

    suspend fun tapToFocus(
        x: Float,
        y: Float
    )

    suspend fun releaseCamera()

    fun getSurfaceRequest(): StateFlow<SurfaceRequest?>

    fun getBarcodeValue(): StateFlow<String?>
}
