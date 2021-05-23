package liou.rayyuan.ebooksearchtaiwan.model

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import liou.rayyuan.ebooksearchtaiwan.utils.DefaultStoreNames

class EventTracker(context: Context) {
    companion object {
        // value to fit Firebase Event name format: underscore
        const val CLICK_INFO_BUTTON = "click_info_button"
        const val CLICK_BACK_TO_TOP_BUTTON = "click_back_to_top_button"
        const val SHOW_EASTER_EGG_01 = "show_easter_egg_01"
        const val CLICK_TO_SEARCH_BUTTON = "click_to_search_button"
        const val OPEN_BOOK_LINK = "open_book_link"
        const val TOP_SELECTED_STORE = "top_selected_store"

        // theme analyze
        const val USER_THEME_CHOSEN = "user_theme_chosen"

        // ISBN scanning analyze
        const val SCANNED_ISBN = "scanned_isbn"
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

    fun generateBookRecordBundle(isFromBestResult: Boolean, bookStoreName: DefaultStoreNames?): Bundle = Bundle().apply {
        putBoolean("choose_from_best_result", isFromBestResult)
        putString("book_store_name", bookStoreName?.defaultName ?: "null")
    }
}