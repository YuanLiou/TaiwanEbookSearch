package com.rayliu.commonmain.data.api

import com.rayliu.commonmain.data.dto.NetworkBookStore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments

class BookStoresApi(
    private val httpClient: HttpClient
) : BookStoresService {
    private val service = "bookstores"
    private val searchRequestBuilder
        get() =
            HttpRequestBuilder().also {
                it.url {
                    appendPathSegments(service)
                }
            }

    override suspend fun getBookStores(): List<NetworkBookStore> = httpClient.get(searchRequestBuilder).body()
}
