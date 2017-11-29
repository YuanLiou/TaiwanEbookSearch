package liou.rayyuan.ebooksearchtaiwan.model.utils

import android.text.TextUtils
import com.google.gson.Gson
import liou.rayyuan.ebooksearchtaiwan.model.entity.BookStores

/**
 * Created by louis383 on 2017/11/29.
 */

object BookStoresUtils {
    fun convertJsonToBookStores(jsonString: String): BookStores? {
        if (!TextUtils.isEmpty(jsonString)) {
            val gson = Gson()
            return gson.fromJson(jsonString, BookStores::class.java)
        }
        return null
    }
}
