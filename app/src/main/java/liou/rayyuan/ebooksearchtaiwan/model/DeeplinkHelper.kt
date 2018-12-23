package liou.rayyuan.ebooksearchtaiwan.model

import android.content.Intent

class DeeplinkHelper {

    // Sample:
    // https://taiwan-ebook-lover.github.io/search?q=xxx

    fun getSearchKeyword(intent: Intent): String? {
        if (canHandle(intent)) {
            val data = intent.data
            data?.let {
                return it.getQueryParameter("q")
            }
        }
        return null
    }

    private fun canHandle(intent: Intent): Boolean {
        val action = intent.action
        if (action == null || action != Intent.ACTION_VIEW) {
            return false
        }
        return true
    }




}