package com.rayliu.commonmain.data

import android.content.res.AssetManager
import com.rayliu.commonmain.data.api.BookStoresService
import com.rayliu.commonmain.data.dto.NetworkBookStore
import io.ktor.client.request.get
import kotlinx.serialization.json.Json

class BookStoresApi(
    private val assetManager: AssetManager,
    private val json: Json
) : BookStoresService {
    override suspend fun getBookStores(keyword: String): List<NetworkBookStore> {
        // TODO: add mock version
        return emptyList()
    }
}
