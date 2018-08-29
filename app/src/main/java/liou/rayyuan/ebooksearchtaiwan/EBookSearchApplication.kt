package liou.rayyuan.ebooksearchtaiwan

import android.app.Application
import android.graphics.Bitmap
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import liou.rayyuan.ebooksearchtaiwan.di.appModules
import liou.rayyuan.ebooksearchtaiwan.model.APIManager
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin

/**
 * Created by louis383 on 2017/12/3.
 */

class EBookSearchApplication : Application() {

    private val apiManager: APIManager by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin(this, appModules)

        val imagePipeline = OkHttpImagePipelineConfigFactory.newBuilder(this, apiManager.httpClient)
        with(imagePipeline) {
            isDownsampleEnabled = true
            setResizeAndRotateEnabledForNetwork(true)
            setBitmapsConfig(Bitmap.Config.RGB_565)
        }
        Fresco.initialize(this, imagePipeline.build())
    }
}
