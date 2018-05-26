package liou.rayyuan.ebooksearchtaiwan.mlscanner

import java.nio.ByteBuffer

interface VisionImageProcessor {
    fun process(data: ByteBuffer, frameMetadata: FrameMetadata)
    fun process(byteArray: ByteArray, frameMetadata: FrameMetadata)
    fun stop()
}