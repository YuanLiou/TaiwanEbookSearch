package com.rayliu.commonmain.data

import android.content.res.AssetManager
import android.util.Log
import com.rayliu.commonmain.data.api.BookStoresService
import com.rayliu.commonmain.data.dto.NetworkBookStore
import com.rayliu.commonmain.utils.loadJsonFromFile
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class BookStoresApi(
    private val assetManager: AssetManager,
    private val json: Json
) : BookStoresService {
    override suspend fun getBookStores(): List<NetworkBookStore> {
        val seconds = (2..5).random()
        delay(TimeUnit.SECONDS.toMillis(seconds.toLong()))
        val rawString = assetManager.loadJsonFromFile("book_store_sample01.json")
        return try {
            json.decodeFromString<List<NetworkBookStore>>(rawString)
        } catch (exception: SerializationException) {
            Log.e("BookStoresApi", Log.getStackTraceString(exception))
            emptyList()
        } catch (exception: IllegalArgumentException) {
            Log.e("BookStoresApi", Log.getStackTraceString(exception))
            emptyList()
        }
    }
}
