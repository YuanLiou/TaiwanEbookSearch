package liou.rayyuan.ebooksearchtaiwan.mlscanner

import java.nio.ByteBuffer

interface VisionImageProcessor {
    fun process(data: ByteBuffer, frameMetadata: FrameMetadata)
    fun stop()
}