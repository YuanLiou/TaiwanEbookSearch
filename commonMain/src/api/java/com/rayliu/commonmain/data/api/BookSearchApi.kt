package com.rayliu.commonmain.data.api

import com.rayliu.commonmain.data.dto.NetworkCrawerResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.appendPathSegments

class BookSearchApi(
    private val httpClient: HttpClient
) : BookSearchService {
    private val service = "searches"
    private val searchRequestBuilder
        get() =
            HttpRequestBuilder().also {
                it.url {
                    appendPathSegments(service)
                }
            }

    override suspend fun postBooks(keyword: String): NetworkCrawerResult {
        val requestBuilder = searchRequestBuilder
        requestBuilder.url {
            parameters.append("q", keyword)
        }
        return httpClient.post(requestBuilder).body()
    }

    override suspend fun postBooks(
        stores: List<String>,
        keyword: String
    ): NetworkCrawerResult {
        val requestBuilder = searchRequestBuilder
        requestBuilder.url {
            parameters.append("q", keyword)
            encodedParameters.appendAll("bookstores[]", stores)
        }
        return httpClient.post(requestBuilder).body()
    }

    override suspend fun getSearchSnapshot(searchId: String): NetworkCrawerResult {
        val requestBuilder = searchRequestBuilder
        requestBuilder.url {
            appendPathSegments(searchId)
        }
        return httpClient.get(requestBuilder).body()
    }
}
