package com.rayliu.commonmain.data

import android.content.res.AssetManager
import android.util.Log
import com.rayliu.commonmain.data.api.BookSearchService
import com.rayliu.commonmain.data.dto.NetworkCrawerResult
import com.rayliu.commonmain.utils.loadJsonFromFile
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class BookSearchApi(
    private val assetManager: AssetManager,
    private val json: Json
) : BookSearchService {
    override suspend fun postBooks(keyword: String): NetworkCrawerResult {
        return NetworkCrawerResult.NOT_FOUND
    }

    override suspend fun postBooks(
        stores: List<String>,
        keyword: String
    ): NetworkCrawerResult {
        val seconds = (5..20).random()
        delay(TimeUnit.SECONDS.toMillis(seconds.toLong()))
        val rawString = assetManager.loadJsonFromFile("book_result_sample01.json")
        return jsonToNetworkCrawerResult(rawString)
    }

    override suspend fun getSearchSnapshot(searchId: String): NetworkCrawerResult {
        val seconds = (5..20).random()
        delay(TimeUnit.SECONDS.toMillis(seconds.toLong()))
        val rawString = assetManager.loadJsonFromFile("book_result_sample02.json")
        return jsonToNetworkCrawerResult(rawString)
    }

    private fun jsonToNetworkCrawerResult(rawString: String): NetworkCrawerResult =
        try {
            json.decodeFromString(NetworkCrawerResult.serializer(), rawString)
        } catch (exception: SerializationException) {
            Log.e("BookSearchApi", Log.getStackTraceString(exception))
            NetworkCrawerResult.NOT_FOUND
        } catch (exception: IllegalArgumentException) {
            Log.e("BookSearchApi", Log.getStackTraceString(exception))
            NetworkCrawerResult.NOT_FOUND
        }
}
