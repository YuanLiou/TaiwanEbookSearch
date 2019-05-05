package liou.rayyuan.ebooksearchtaiwan.mlscanner

import android.util.Log
import android.util.Size
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

class FrameVisionProcessor(private var visionImageProcessor: VisionImageProcessor?,
                           private var cameraInformationCollector: CameraInformationCollector?): LifecycleObserver {
    private val processingRunnable = FrameProcessingRunnable()
    private var processingThread: Thread? = Thread(processingRunnable)
    private val processorLock = Object()    // for synchronization use

    fun setupLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun start() {
        val isFirstStart = processingThread?.state == Thread.State.NEW
        if (isFirstStart) {
            processingThread?.start()
            processingRunnable.active = true
        }
    }

    fun setNextFrame(bytes: ByteArray) {
        processingRunnable.data = bytes
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun release() {
        synchronized(processorLock) {
            stop()
            visionImageProcessor?.stop()

            visionImageProcessor = null
            cameraInformationCollector = null
        }
    }

    private fun stop() {
        processingRunnable.active = false
        processingThread = processingThread?.let {
            it.join()
            Log.i("FrameVisionProcessor", "processing thread state = ${it.state}")
            null
        }
    }

    private inner class FrameProcessingRunnable: Runnable {
        private val dataLock = Object()    // synchronization use

        internal var active = false
            set(value) {
                synchronized(dataLock) {
                    field = value
                    dataLock.notifyAll()
                }
            }

        internal var data: ByteArray? = null
            set(value) {
                synchronized(dataLock) {
                    field = value
                    dataLock.notifyAll()
                }
            }

        override fun run() {
            var bytes: ByteArray? = null
            while (true) {
                synchronized(dataLock) {
                    while (active && data == null) {
                        try {
                            dataLock.wait()
                        } catch (e: InterruptedException) {
                            Log.e("FrameProcessingRunnable", Log.getStackTraceString(e))
                            return
                        }
                    }

                    if (!active) {
                        return
                    }

                    bytes = data
                    data = null    // clean up
                }

                synchronized(processorLock) {
                    bytes?.run {
                        cameraInformationCollector?.let {
                            val frameMetaData = FrameMetadata(
                                    it.getCameraPreviewSize().width,
                                    it.getCameraPreviewSize().height,
                                    it.getCameraOrientation(),
                                    it.getCameraFacingDirection()
                            )

//                            Log.i("FrameProcessingRunnable", "processing image")
                            visionImageProcessor?.process(this, frameMetaData)
                        }
                    }
                }
            }
        }
    }

    interface CameraInformationCollector {
        fun getCameraPreviewSize(): Size
        fun getCameraOrientation(): Int
        fun getCameraFacingDirection(): Int
    }
}