package liou.rayyuan.ebooksearchtaiwan

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco

/**
 * Created by louis383 on 2017/12/3.
 */

class EBookSearchApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
    }
}
