package liou.rayyuan.ebooksearchtaiwan.camerapreview

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.view.widget.AutoFitTextureView
import java.nio.ByteBuffer
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
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
class CameraPreviewManager(private val context: Context, private val textureView: AutoFitTextureView,
                           private val cameraCallback: OnCameraPreviewCallback,
                           private val displaySizeRequireHandler: OnDisplaySizeRequireHandler): LifecycleObserver {
    enum class CameraState {
        CLOSED,
        OPENED,
        PREVIEW
    }
    var cameraState = CameraState.CLOSED
    var previewSize: Size? = null
    var facing: Int? = null
    var rotationConstraintNum: Int = 0
        get() {
            return field / 90
        }

    private var uiHandler: UIHandler? = null
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    private var imageReader: ImageReader? = null
    private var cameraCaptureSession: CameraCaptureSession? = null
    private var cameraId: String? = null
    private var cameraCharacteristics: CameraCharacteristics? = null
    private var camera: CameraDevice? = null
    private val cameraOpenCloseLock: Semaphore = Semaphore(1)

    private var isAborted: Boolean = false
    private val imageBufferCounts = 4
    private val aspectRatioTolerance = 0.005

    private val orientationMap = mapOf(
            Pair(Surface.ROTATION_0, 0),
            Pair(Surface.ROTATION_90, 90),
            Pair(Surface.ROTATION_180, 180),
            Pair(Surface.ROTATION_270, 270)
    )

    fun setupLifeCycleOwner(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun start() {
        doWithCameraPermission {
            startThread()
            initCamera()

            if (textureView.isAvailable) {
                configureTransform(textureView.width, textureView.height)
            } else {
                textureView.surfaceTextureListener = SurfaceTextureCallback()
            }
            Log.i("CameraPreviewManager", "start")
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun release() {
        if (isAborted) {
            return
        }

        try {
            cameraState = CameraState.CLOSED
            cameraOpenCloseLock.acquire()
            cameraCaptureSession?.close()
            cameraCaptureSession = null
            camera?.close()
            camera = null
            imageReader?.close()
            imageReader = null
            stopThread()
            Log.i("CameraPreviewManager", "released")
        } catch (e: InterruptedException) {
            Log.e("CameraPreviewManager", Log.getStackTraceString(e))
        } finally {
            cameraOpenCloseLock.release()
        }
    }

    // Actually checked in the lambda function
    @SuppressLint("MissingPermission")
    private fun initCamera() {
        doWithCameraPermission {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            if (!setupCameraParams(cameraManager)) {
                val message = context.getString(R.string.camera_opening_failed)
                cameraCallback.onError(message)
                return
            }

            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                val message = context.getString(R.string.camera_opening_timeout)
                cameraCallback.onError(message)
            }
            cameraManager.openCamera(cameraId, cameraDeviceCallback(), backgroundHandler)
        }
    }

    private fun setupCameraParams(cameraManager: CameraManager): Boolean {
        for (cameraId in cameraManager.cameraIdList) {
            val characteristic = cameraManager.getCameraCharacteristics(cameraId)

            // Ignore Front Facing Camera
            val facing = characteristic.get(CameraCharacteristics.LENS_FACING)
            if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                continue
            }

            // Build imageReader
            val configurationMap = characteristic.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val largestSize = configurationMap.getOutputSizes(ImageFormat.JPEG).maxWith(CompareAreaSize())

            largestSize?.let {
                imageReader = ImageReader.newInstance(it.width, it.height, ImageFormat.JPEG, imageBufferCounts)
                imageReader?.setOnImageAvailableListener({ reader ->
                    backgroundHandler?.post({
                        val image = reader?.acquireLatestImage()

                        image?.planes?.get(0)?.buffer?.let {
                            val data = ByteArray(it.remaining())
                            it.get(data)
                        }?.run {
                            cameraCallback.onByteBufferGenerated(this)
                        }

                        image?.close()
                    })
                }, backgroundHandler)
            } ?: return false

            this.cameraCharacteristics = characteristic
            this.cameraId = cameraId
            this.facing = facing
            return true
        }

        return false
    }

    private fun configureTransform(width: Int, height: Int) {
        requireNotNull(cameraCharacteristics)
        if (!textureView.isAvailable) {
            return
        }

        cameraCharacteristics?.run {
            val map = get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

            val largestSize = map.getOutputSizes(ImageFormat.JPEG).maxWith(CompareAreaSize())

            // find the rotation of the device relative to the native device orientation
            val deviceOrientation = displaySizeRequireHandler.getDisplayOrientation()
            val displaySize = Point()
            displaySizeRequireHandler.getDisplaySize(displaySize)

            // find the rotation of the device relative to the camera sensor
            val totalRotation = sensorToDeviceRotation(deviceOrientation)
            rotationConstraintNum = totalRotation

            // swap the dimension if needed
            val swappedDimension = totalRotation == 90 || totalRotation == 270
            val rotatedViewWidth = if (swappedDimension) height else width
            val rotatedViewHeight = if (swappedDimension) width else height

            // preview should not be larger than display size and 1080p
            val maxPreviewWidth = swappedDimension.let { shouldSwap ->
                var result = if (shouldSwap) displaySize.y else displaySize.x
                if (result > 1920) result = 1920
                result
            }
            val maxPreviewHeight = swappedDimension.let { shouldSwap ->
                var result = if (shouldSwap) displaySize.x else displaySize.y
                if (result > 1080) result = 1080
                result
            }

            // find best preview size for these view dimensions and configured JPEG size
            val previewSize = largestSize?.let {
                chooseOptimalSize(map.getOutputSizes(SurfaceTexture::class.java),
                        rotatedViewWidth, rotatedViewHeight, maxPreviewWidth, maxPreviewHeight,
                        it)
            }

            previewSize?.run {

                if (swappedDimension) {
                    textureView.setAspectRatio(this.height, this.width)
                } else {
                    textureView.setAspectRatio(this.width, this.height)
                }

                // find rotation of device in degrees
                val rotation = (360 - orientationMap[deviceOrientation]!!) % 360

                val matrix = Matrix()
                val viewRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
                val centerX = viewRect.centerX()
                val centerY = viewRect.centerY()

                if (deviceOrientation == Surface.ROTATION_90 || deviceOrientation == Surface.ROTATION_270) {
                    val bufferRect = RectF(0f, 0f, this.height.toFloat(), this.width.toFloat())
                    bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
                    matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
                    val scale = Math.max(
                            height.toFloat() / this.height,
                            width.toFloat() / this.width
                    )
                    matrix.postScale(scale, scale, centerX, centerY)
                }
                matrix.postRotate(rotation.toFloat(), centerX, centerY)
                textureView.setTransform(matrix)

                if (this@CameraPreviewManager.previewSize == null || !checkAspectsEqual(previewSize, this@CameraPreviewManager.previewSize!!)) {
                    this@CameraPreviewManager.previewSize = previewSize
                    if (cameraState != CameraState.CLOSED) {
                        startPreviewing()
                    }
                }
            }
        } ?: return
    }

    private fun checkAspectsEqual(firstSize: Size, secondSize: Size): Boolean {
        val firstAspect = firstSize.width / firstSize.height.toDouble()
        val secondAspect = secondSize.width / secondSize.height.toDouble()
        return Math.abs(firstAspect - secondAspect) <= aspectRatioTolerance
    }

    private fun sensorToDeviceRotation(deviceOrientationRaw: Int): Int {
        return cameraCharacteristics?.let {
            val sensorOrientation = it.get(CameraCharacteristics.SENSOR_ORIENTATION)
            val deviceOrientation = orientationMap[deviceOrientationRaw] ?: 0
            (sensorOrientation - deviceOrientation + 360) % 360
        } ?: -1
    }

    private inner class CompareAreaSize : Comparator<Size> {
        override fun compare(first: Size, second: Size): Int {
            // use Long ensure multiplication won't overflow
            val firstAreaSize = first.width.toLong() * first.height
            val secondAreaSize = second.width.toLong() * second.height
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
            cameraState = CameraState.OPENED
            cameraOpenCloseLock.release()
            camera = cameraDevice

            if (previewSize != null && textureView.isAvailable) {
                startPreviewing()
            }
        }

        override fun onDisconnected(cameraDevice: CameraDevice?) {
            cameraState = CameraState.CLOSED
            cameraOpenCloseLock.release()
            release()
        }

        override fun onError(cameraDevice: CameraDevice?, error: Int) {
            cameraState = CameraState.CLOSED
            cameraOpenCloseLock.release()
            val message = context.getString(R.string.camera_opening_failed)
            cameraCallback.onError(message)
            release()
            Log.e("CameraPreviewManager", "Camera Open Error, Code is =$error")
        }
    }

    private fun startPreviewing() {
        // TextureView surface
        val surfaceTexture = textureView.surfaceTexture ?: return
        surfaceTexture.setDefaultBufferSize(previewSize?.width ?: 1280, previewSize?.height ?: 720)
        val surface = Surface(surfaceTexture)

        // ImageReader surface
        val imageReaderSurface = imageReader?.surface

        val captureRequestBuilder = camera?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequestBuilder?.addTarget(surface)
        captureRequestBuilder?.addTarget(imageReaderSurface)

        camera?.createCaptureSession(listOf(surface, imageReaderSurface), object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(cameraCaptureSession: CameraCaptureSession?) {
                this@CameraPreviewManager.cameraCaptureSession = cameraCaptureSession

                captureRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                val previewRequest = captureRequestBuilder?.build()
                cameraCaptureSession?.setRepeatingRequest(previewRequest, null, backgroundHandler)

                cameraState = CameraState.PREVIEW
            }

            override fun onConfigureFailed(captureSession: CameraCaptureSession?) {
                val message = context.getString(R.string.camera_opening_failed)
                uiHandler?.obtainMessage(UIHandler.sendMessage, message)?.sendToTarget()
            }
        }, backgroundHandler)
    }

    private fun startThread() {
        backgroundThread = HandlerThread("CameraBackground")
        backgroundThread?.start()
        backgroundHandler = Handler(backgroundThread?.looper)

        uiHandler = UIHandler(cameraCallback)
    }

    private fun stopThread() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
            uiHandler = null
        } catch (e: InterruptedException) {
            Log.e("CameraPreviewManager", Log.getStackTraceString(e))
        }
    }

    private inline fun doWithCameraPermission(action: () -> Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            isAborted = false
            action()
        } else {
            isAborted = true
            cameraCallback.shouldRequestPermission()
        }
    }

    inner class SurfaceTextureCallback : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
        }

        override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture?, width: Int, height: Int) {
            configureTransform(width, height)
        }

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
            release()
            previewSize = null
            Log.i("CameraPreviewManager", "surfaceDestroyed")
            return true
        }

        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture?, width: Int, height: Int) {
            configureTransform(width, height)
            Log.i("CameraPreviewManager", "surfaceCreated")
        }
    }

    class UIHandler(failureCallback: OnCameraPreviewCallback): Handler(Looper.getMainLooper()) {
        companion object {
            const val sendMessage: Int = 1001
        }

        private var failedCallback: OnCameraPreviewCallback = failureCallback

        override fun handleMessage(message: Message?) {
            when (message?.what) {
                sendMessage -> {
                    val errorMessage: String = message.obj as String
                    failedCallback.onError(errorMessage)
                }
                else -> super.handleMessage(message)
            }
        }
    }

    interface OnCameraPreviewCallback {
        fun shouldRequestPermission()
        fun onError(message: String)
        fun onByteBufferGenerated(buffer: ByteBuffer?)
    }

    interface OnDisplaySizeRequireHandler {
        fun getDisplaySize(point: Point)
        fun getDisplayOrientation(): Int
    }
}