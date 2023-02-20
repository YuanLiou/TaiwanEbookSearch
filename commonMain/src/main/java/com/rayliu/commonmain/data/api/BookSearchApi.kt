package com.rayliu.commonmain.data.api

import com.rayliu.commonmain.BuildConfig
import io.ktor.client.*
import io.ktor.client.request.*
import com.rayliu.commonmain.data.dto.NetworkCrawerResult
import io.ktor.client.call.body

class BookSearchApi(
    private val httpClient: HttpClient
) : BookSearchService {

    private val url = BuildConfig.HOST_URL + "searches"
    private val searchQuery = "?q="

    override suspend fun postBooks(keyword: String): NetworkCrawerResult {
        return httpClient.post(url + searchQuery + keyword).body()
    }

    override suspend fun postBooks(stores: List<String>, keyword: String): NetworkCrawerResult {
        val storesString = stores.joinToString("&bookstores[]=", prefix = "&bookstores[]=")
        return httpClient.post(url + searchQuery + keyword + storesString).body()
    }

    override suspend fun getSearchSnapshot(searchId: String): NetworkCrawerResult {
        val snapshotUrl = url + "/"
        return httpClient.get(snapshotUrl + searchId).body()
    }
}