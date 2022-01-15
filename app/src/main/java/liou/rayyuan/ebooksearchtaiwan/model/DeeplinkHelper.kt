package liou.rayyuan.ebooksearchtaiwan.model

import android.content.Intent

class DeeplinkHelper {

    // Sample:
    // https://taiwan-ebook-lover.github.io/search?q=xxx
    // https://taiwan-ebook-lover.github.io/searches/bZwSu2Ecl0yzn2G3nkuv

    fun getSearchKeyword(intent: Intent): String? {
        if (canHandle(intent)) {
            val data = intent.data
            if (data != null) {
                return data.getQueryParameter("q")
            }
        }
        return null
    }

    fun getSearchId(intent: Intent): String? {
        if (canHandle(intent)) {
            val data = intent.data
            if (data != null) {
                return data.lastPathSegment
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
