package liou.rayyuan.ebooksearchtaiwan.mlscanner

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage

class BarcodeVisionProcessor: BaseVisionProcessor<List<FirebaseVisionBarcode>>() {
    private val detector: FirebaseVisionBarcodeDetector
    var visionProcessListener: VisionProcessListener? = null

    init {
        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_EAN_13)
                .build()
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)
    }

    override fun stop() {
        detector.close()
    }

    override fun detectInImage(image: FirebaseVisionImage): Task<List<FirebaseVisionBarcode>> {
//        visionProcessListener?.onVisionProcessDebugUse(image.bitmapForDebugging)
        return detector.detectInImage(image)
    }

    override fun onDetectionSucceed(result: List<FirebaseVisionBarcode>, frameMetadata: FrameMetadata) {
        if (result.isNotEmpty()) {
            for (barcode in result) {
                barcode.rawValue?.let {
                    if (it.isNotBlank()) {
                        visionProcessListener?.onVisionProcessSucceed(it)
                    }
                }
                Log.i("BarcodeVisionProcessor", """Barcode result: ${barcode.rawValue}""")
            }
        }
    }

    override fun onDetectionFailed(exception: Exception) {
        Log.e("BarcodeVisionProcessor", Log.getStackTraceString(exception))
    }
}