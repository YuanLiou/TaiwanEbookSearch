package liou.rayyuan.ebooksearchtaiwan.mlscanner

interface VisionProcessListener {
    fun onVisionProcessSucceed(result: String)
    // Debug only
//    fun onVisionProcessDebugUse(bitmap: Bitmap)
}