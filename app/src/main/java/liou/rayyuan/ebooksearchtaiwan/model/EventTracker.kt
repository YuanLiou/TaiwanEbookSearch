package liou.rayyuan.ebooksearchtaiwan.model

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.rayliu.commonmain.data.DefaultStoreNames

class EventTracker(context: Context) {
    companion object {
        // value to fit Firebase Event name format: underscore
        const val CLICK_INFO_BUTTON = "click_info_button"
        const val CLICK_BACK_TO_TOP_BUTTON = "click_back_to_top_button"
        const val SHOW_EASTER_EGG_01 = "show_easter_egg_01"
        const val CLICK_TO_SEARCH_BUTTON = "click_to_search_button"
        const val TOP_SELECTED_STORE = "top_selected_store"
    }

    // TODO:: make an abstract layer for 3rd party implementation
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)

    fun logEvent(eventName: String, bundle: Bundle? = null) {
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    fun logTopSelectedStoreName(storeNames: List<DefaultStoreNames>) {
        logEvent(TOP_SELECTED_STORE, Bundle().apply {
            putString("storeName", storeNames.first().defaultName)
        })
    }
}