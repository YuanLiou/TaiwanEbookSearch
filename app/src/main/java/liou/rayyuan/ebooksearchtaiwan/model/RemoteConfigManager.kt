package liou.rayyuan.ebooksearchtaiwan.model

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.R
import java.util.concurrent.TimeUnit

/**
 * Created by louis383 on 2018/8/31.
 */
class RemoteConfigManager {

    companion object {
        const val COLOR_BACK_TO_TOP_BUTTON_KEY = "use_color_back_to_top_button"
        const val KEYBOARD_BACK_TO_TOP_ICON_KEY = "use_keyboard_icon_on_top_list"
    }

    val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    init {
        val firebaseRemoteConfigSettings =
                FirebaseRemoteConfigSettings.Builder().apply {
                    setDeveloperModeEnabled(BuildConfig.DEBUG)
                }.build()
        firebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings)
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_default)
        firebaseRemoteConfig.activateFetched()    // active last time fetch values
    }

    fun start() {
        val cacheExpiration = if (BuildConfig.DEBUG) 0 else TimeUnit.HOURS.toSeconds(12)
        firebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener {
            if (it.isSuccessful) {
                firebaseRemoteConfig.activateFetched()
                Log.i("RemoteConfigManager", "RemoteConfig Fetched.")
            }
        }
    }
}