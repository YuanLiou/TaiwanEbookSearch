package liou.rayyuan.ebooksearchtaiwan

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import liou.rayyuan.ebooksearchtaiwan.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin

/**
 * Created by louis383 on 2017/12/3.
 */

class EBookSearchApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            if (BuildConfig.DEBUG) {
                AndroidLogger()
            }

            androidContext(this@EBookSearchApplication)
            modules(appModules)
        }
        AndroidThreeTen.init(this)
    }
}
