package liou.rayyuan.ebooksearchtaiwan

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.rayliu.commonmain.di.sharedModule
import liou.rayyuan.ebooksearchtaiwan.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin

class EBookSearchApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            if (BuildConfig.DEBUG) {
                AndroidLogger()
                FirebaseAnalytics.getInstance(this@EBookSearchApplication).setAnalyticsCollectionEnabled(false)
            }

            androidContext(this@EBookSearchApplication)
            modules(sharedModule, appModule)
        }
    }
}
