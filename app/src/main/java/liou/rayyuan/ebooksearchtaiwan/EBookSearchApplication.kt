package liou.rayyuan.ebooksearchtaiwan

import android.app.Application
import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.firebase.analytics.FirebaseAnalytics
import liou.rayyuan.ebooksearchtaiwan.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin

/**
 * Created by louis383 on 2017/12/3.
 */

class EBookSearchApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            if (BuildConfig.DEBUG) {
                AndroidLogger()
                FirebaseAnalytics.getInstance(this@EBookSearchApplication).setAnalyticsCollectionEnabled(false)
            }

            androidContext(this@EBookSearchApplication)
            modules(appModules)
        }
    }
}
