package liou.rayyuan.ebooksearchtaiwan.misc

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.rayliu.commonmain.data.DefaultStoreNames

class EventTracker(
    context: Context
) {
    companion object {
        // value to fit Firebase Event name format: underscore
        const val TOP_SELECTED_STORE = "top_selected_store"
    }

    // TODO:: make an abstract layer for 3rd party implementation
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)

    fun logEvent(
        eventName: String,
        bundle: Bundle? = null
    ) {
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    fun logTopSelectedStoreName(storeNames: List<DefaultStoreNames>) {
        logEvent(
            TOP_SELECTED_STORE,
            Bundle().apply {
                putString("storeName", storeNames.first().defaultName)
            }
        )
    }
}
