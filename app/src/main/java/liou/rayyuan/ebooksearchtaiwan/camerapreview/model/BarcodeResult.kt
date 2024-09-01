package liou.rayyuan.ebooksearchtaiwan.camerapreview.model

import android.graphics.Rect

data class BarcodeResult(
    val barcodeValue: String,
    val boundingBox: Rect?,
    val imageWidth: Int,
    val imageHeight: Int
) {
    fun isBarcodeAvailable(): Boolean = barcodeValue.isNotEmpty()
}
