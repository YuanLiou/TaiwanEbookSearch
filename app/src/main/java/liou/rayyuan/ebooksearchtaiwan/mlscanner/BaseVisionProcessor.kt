package liou.rayyuan.ebooksearchtaiwan.mlscanner

import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseVisionProcessor<T>: VisionImageProcessor {

    private val shouldThrottle = AtomicBoolean(false)

    override fun process(data: ByteBuffer, frameMetadata: FrameMetadata) {
        if (shouldThrottle.get()) {
            return
        }

        val firebaseVisionImageMetaData = FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setWidth(frameMetadata.width)
                .setHeight(frameMetadata.height)
                .setRotation(frameMetadata.rotation)
                .build()

        detectInVisionImage(FirebaseVisionImage.fromByteBuffer(data, firebaseVisionImageMetaData),
                frameMetadata)
    }

    override fun stop() {
    }

    private fun detectInVisionImage(image: FirebaseVisionImage, metadata: FrameMetadata) {
        detectInImage(image).addOnSuccessListener {
            shouldThrottle.set(false)
            onDetectionSucceed(it, metadata)
        }.addOnFailureListener {
            shouldThrottle.set(false)
            onDetectionFailed(it)
        }
        // Start throttle until this frame of input has been processed.
        shouldThrottle.set(true)
    }

    protected abstract fun detectInImage(image: FirebaseVisionImage): Task<T>
    protected abstract fun onDetectionSucceed(result: T, frameMetadata: FrameMetadata)
    protected abstract fun onDetectionFailed(exception: Exception)
}