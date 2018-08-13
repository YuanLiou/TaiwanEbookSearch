package liou.rayyuan.ebooksearchtaiwan

import android.app.Application
import android.graphics.Bitmap
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import liou.rayyuan.ebooksearchtaiwan.model.APIManager
import liou.rayyuan.ebooksearchtaiwan.model.EventTracker

/**
 * Created by louis383 on 2017/12/3.
 */

class EBookSearchApplication : Application() {

    val apiManager: APIManager by lazy { APIManager() }
    val eventTracker: EventTracker by lazy { EventTracker(this) }

    override fun onCreate() {
        super.onCreate()

        val imagePipeline = OkHttpImagePipelineConfigFactory.newBuilder(this, apiManager.httpClient)
        with(imagePipeline) {
            isDownsampleEnabled = true
            setResizeAndRotateEnabledForNetwork(true)
            setBitmapsConfig(Bitmap.Config.RGB_565)
        }

        Fresco.initialize(this, imagePipeline.build())
    }
}
