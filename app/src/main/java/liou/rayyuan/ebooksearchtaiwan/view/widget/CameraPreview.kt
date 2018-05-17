package liou.rayyuan.ebooksearchtaiwan.view.widget

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.ActivityCompat
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import kotlin.math.sign

/**
 * Camera initializing processing is
 *  - Init surfaceView / TextureView. should make sure [SurfaceView] or [TextureView] is initialized.
 *  - Check **Camera Permission**. In this phase, it needs a callback to
 *  main Activity. Otherwise, Activity can't display camera previewing if not permitted.
 *  - If camera permission is grant. Run the background thread for camera things.
 *  - Make sure background thread and surfaceView is all Ready. Init Camera.
 *  - After camera initialization. Put Preview on the screen.
 *
 *  - Additional info if use TextureView
 *    - in [TextureView] it needs to handle rotation and image matrix ourselves.
 *    - needs to get orientation of the device and the camera sensor
 *    - check [setupCameraParams(cameraManager: CameraManager)] and [configureTransform()] methods for more infos.
 */
class CameraPreview(context: Context, attrs: AttributeSet): ViewGroup(context, attrs), LifecycleObserver {
    var failureCallback: OnCameraPreviewFailureCallback? = null
    var displaySizeRequireHandler: OnDisplaySizeRequireHandler? = null
    var displayOrientation: Int = 0

    private val textureView: AutoFitTextureView = AutoFitTextureView(context, attrs)

    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    private var previewSize: Size? = null
    private var imageReader: ImageReader? = null
    private var cameraId: String? = null
    private var camera: CameraDevice? = null

    private var isAborted: Boolean = false

    init {
        textureView.surfaceTextureListener = SurfaceTextureCallback()
        addView(textureView)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (index in 0 until childCount) {
            getChildAt(index).layout(0, 0, width, height)
            Log.i("CameraPreview", "Assigned view = $index")
        }
    }

    fun setupLifeCycleOwner(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun start() {
        doWithCameraPermission {
            if (textureView.isAvailable) {
                startBackgroundThread()
                initCamera()
                Log.i("CameraPreview", "start")
            }
            Log.i("CameraPreview", "start isSurfaceAvailable = " + textureView.isAvailable)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun release() {
        if (isAborted) {
            return
        }

        camera?.close()
        camera = null
        imageReader?.close()
        imageReader = null
        stopBackgroundThread()
        Log.i("CameraPreview", "released")
    }

    // Actually checked in the lambda function
    @SuppressLint("MissingPermission")
    private fun initCamera() {
        doWithCameraPermission {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            setupCameraParams(cameraManager)
            configureTransform()
            cameraManager.openCamera(cameraId, cameraDeviceCallback(), backgroundHandler)
        }
    }

    private fun setupCameraParams(cameraManager: CameraManager) {
        for (cameraId in cameraManager.cameraIdList) {
            val characteristic = cameraManager.getCameraCharacteristics(cameraId)

            // Ignore Front Facing Camera
            val facing = characteristic.get(CameraCharacteristics.LENS_FACING)
            if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                continue
            }

            val configurationMap = characteristic.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    ?: continue

            // Get all available size for camera image capturing. because it differs in different models
            val largestSize = configurationMap.getOutputSizes(ImageFormat.JPEG).maxWith(CompareAreaSize())
            Log.i("CameraPreview", "largest size of previewing, height = " + largestSize?.height
                    + " , width = " + largestSize?.width)
            imageReader = ImageReader.newInstance(
                    largestSize?.width ?: 1280,
                    largestSize?.height ?: 720,
                    ImageFormat.JPEG, 1)

            // TODO:: Capture Image Related Actions

            // Handler Device Orientations
            val cameraSensorOrientation = characteristic.get(CameraCharacteristics.SENSOR_ORIENTATION)
            var shouldSwapDimension = false
            when (displayOrientation) {
                Surface.ROTATION_0, Surface.ROTATION_180 -> {
                    if (cameraSensorOrientation == 90 || cameraSensorOrientation == 270) {
                        shouldSwapDimension = true
                    }
                }
                Surface.ROTATION_90, Surface.ROTATION_270 -> {
                    if (cameraSensorOrientation == 0 || cameraSensorOrientation == 180) {
                        shouldSwapDimension = true
                    }
                }
                else -> { Log.e("CameraPreview", "Camera orientation is not supported.")}
            }
            Log.i("CameraPreview", "display orientation = $displayOrientation, camera orientation = $cameraSensorOrientation")

            // If can not get the display size. Give up rotation handling.
            val displaySize = Point()
            displaySizeRequireHandler?.getDisplaySize(displaySize)
            var rotatedPreviewWidth = textureView.width
            var rotatedPreviewHeight = textureView.height
            var maxPreviewWidth = displaySize.x
            var maxPreviewHeight = displaySize.y

            if (shouldSwapDimension) {
                rotatedPreviewWidth = textureView.height
                rotatedPreviewHeight = textureView.width
                maxPreviewWidth = displaySize.y
                maxPreviewHeight = displaySize.x
            }
            Log.i("CameraPreview", "should swap dimension = $shouldSwapDimension")

            if (maxPreviewWidth > rotatedPreviewWidth) {
                maxPreviewWidth = if (shouldSwapDimension) rotatedPreviewHeight else rotatedPreviewWidth
            }

            if (maxPreviewHeight > rotatedPreviewHeight) {
                maxPreviewHeight = if (shouldSwapDimension) rotatedPreviewWidth else rotatedPreviewHeight
            }

            Log.i("CameraPreview", "rotatedWidth = $rotatedPreviewWidth, rotatedHeight = $rotatedPreviewHeight")
            Log.i("CameraPreview", "maxPreviewWidth = $maxPreviewWidth, maxPreviewHeight = $maxPreviewHeight")

            previewSize = chooseOptimalSize(configurationMap.getOutputSizes(SurfaceTexture::class.java),
                    rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth, maxPreviewHeight,
                    largestSize!!)

            previewSize?.let {
                // Fit the aspect ratio of TextureView to the optimal size
                // If previewSize is not available then ignore adjust aspect ratio
                val orientation = context.resources.configuration.orientation
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    textureView.setAspectRatio(it.width, it.height)
                } else {
                    textureView.setAspectRatio(it.height, it.width)
                }
            }

            this.cameraId = cameraId
        }
    }

    private fun configureTransform() {
        if (previewSize == null) {
            return
        }

        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, textureView.width.toFloat(), textureView.height.toFloat())
        val bufferRect = previewSize?.let { RectF(0f, 0f, it.height.toFloat() , it.width.toFloat()) }
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()

        if (displayOrientation == Surface.ROTATION_90 || displayOrientation == Surface.ROTATION_270) {
            bufferRect?.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale = Math.max(
                    (textureView.height / previewSize!!.height).toFloat(),
                    (textureView.width / previewSize!!.width).toFloat())
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate((90 * (displayOrientation - 2)).toFloat(), centerX, centerY)
        } else if (displayOrientation == Surface.ROTATION_180) {
            matrix.postRotate(180f, centerX, centerY)
        }

        textureView.setTransform(matrix)
    }

    private inner class CompareAreaSize : Comparator<Size> {
        override fun compare(first: Size?, second: Size?): Int {
            // use Long ensure multiplication won't overflow
            val firstAreaSize = (first?.width?.times(first.height))?.toLong()
            val secondAreaSize = (second?.width?.times(second.height))?.toLong()
            if (firstAreaSize == null || secondAreaSize == null) {
                return 0
            }

            return (firstAreaSize - secondAreaSize).sign
        }
    }

    private fun chooseOptimalSize(sizes: Array<Size>, textureViewWidth: Int,
                                  textureViewHeight: Int, maxWidth: Int, maxHeight: Int,
                                  aspectRatio: Size): Size? {

        // The supported resolution that are at least as big as the preview surface
        val bigEnoughSizes = mutableListOf<Size>()
        // The supported resolution that are smaller than the preview Surface
        val notBigEnoughSizes = mutableListOf<Size>()

        val width = aspectRatio.width
        val height = aspectRatio.height
        for (size in sizes) {
            if (size.width <= maxWidth && size.height <= maxHeight &&
                    size.height == size.width * height / width) {

                if (size.width >= textureViewWidth && size.height >= textureViewHeight) {
                    bigEnoughSizes.add(size)
                } else {
                    notBigEnoughSizes.add(size)
                }
            }
        }

        // Pick the smallest sizes in these big enough sizes. If there is no big enough size,
        // pick the largest sizes in the NOT big enough sizes.
        if (bigEnoughSizes.isNotEmpty()) {
            return bigEnoughSizes.minWith(CompareAreaSize())
        }

        if (notBigEnoughSizes.isNotEmpty()) {
            return notBigEnoughSizes.maxWith(CompareAreaSize())
        }

        // No proper size
        return sizes[0]
    }

    private fun cameraDeviceCallback(): CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice?) {
            camera = cameraDevice
            startPreviewing()
        }

        override fun onDisconnected(cameraDevice: CameraDevice?) {
            release()
        }

        override fun onError(cameraDevice: CameraDevice?, error: Int) {
            failureCallback?.onError("Failed to open camera")
            release()
            Log.e("CameraPreview", "Camera Open Error, Code is =$error")
        }
    }

    private fun startPreviewing() {
        val surfaceTexture = textureView.surfaceTexture ?: return

        surfaceTexture.setDefaultBufferSize(previewSize?.width ?: 1280, previewSize?.height ?: 720)
        val surface = Surface(surfaceTexture)

        val captureRequestBuilder = camera?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequestBuilder?.addTarget(surface)
        camera?.createCaptureSession(listOf(surface, imageReader?.surface), object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(cameraCaptureSession: CameraCaptureSession?) {
                captureRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                val previewRequest = captureRequestBuilder?.build()
                cameraCaptureSession?.setRepeatingRequest(previewRequest, null, backgroundHandler)
            }

            override fun onConfigureFailed(p0: CameraCaptureSession?) {
                val message = "trying to start camera previewing failed."
                // Thread issue
//                this@CameraPreview.failureCallback?.onError(message)
                Log.e("CameraPreview", message)
            }
        }, backgroundHandler)
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground")
        backgroundThread?.start()
        backgroundHandler = Handler(backgroundThread?.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            Log.e("CameraPreview", Log.getStackTraceString(e))
        }
    }

    private inline fun doWithCameraPermission(action: () -> Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            isAborted = false
            action()
        } else {
            isAborted = true
            failureCallback?.shouldRequestPermission()
        }
    }

    inner class SurfaceTextureCallback : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {}

        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {
            configureTransform()
        }

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
            release()
            Log.i("CameraPreview", "surfaceDestroyed")
            return true
        }

        override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, p1: Int, p2: Int) {
            start()
            Log.i("CameraPreview", "surfaceCreated")
        }
    }

    interface OnCameraPreviewFailureCallback {
        fun shouldRequestPermission()
        fun onError(message: String)
    }

    interface OnDisplaySizeRequireHandler {
        fun getDisplaySize(point: Point)
    }
}