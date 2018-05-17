package liou.rayyuan.ebooksearchtaiwan.view.widget

import android.content.Context
import android.support.annotation.IntRange
import android.util.AttributeSet
import android.view.TextureView

class AutoFitTextureView(context: Context, attrs: AttributeSet): TextureView(context, attrs) {
    private var retroWidth = 0
    private var retroHeight = 0

    fun setAspectRatio(@IntRange(from = 0) width: Int, @IntRange(from = 0) height: Int) {
        require(width >= 0 && height >= 0, {"Width or Height can not be negative."})

        retroWidth = width
        retroHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        if (width == 0 || height == 0) {
            setMeasuredDimension(width, height)
            return
        }

        if (width < height * retroWidth / retroHeight) {
            setMeasuredDimension(width, width * retroHeight / retroWidth)
        } else {
            setMeasuredDimension(height * retroWidth / retroHeight, height)
        }
    }
}