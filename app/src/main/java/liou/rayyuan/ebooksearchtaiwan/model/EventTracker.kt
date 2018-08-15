package liou.rayyuan.ebooksearchtaiwan.model

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class EventTracker(context: Context) {
    companion object {
        // value to fit Firebase Event name format: underscore
        const val CLICK_INFO_BUTTON = "click_info_button"
        const val CLICK_BACK_TO_TOP_BUTTON = "click_back_to_top_button"
        const val SHOW_EASTER_EGG_01 = "show_easter_egg_01"
        const val CLICK_TO_SEARCH_BUTTON = "click_to_search_button"
        const val OPEN_BOOK_LINK = "open_book_link"
    }

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)

    fun logEvent(eventName: String, bundle: Bundle? = null) {
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    fun generateBookRecordBundle(isFromBestResult: Boolean, bookStoreName: String): Bundle = Bundle().apply {
        putBoolean("choose_from_best_result", isFromBestResult)
        putString("book_store_name", bookStoreName)
    }
}