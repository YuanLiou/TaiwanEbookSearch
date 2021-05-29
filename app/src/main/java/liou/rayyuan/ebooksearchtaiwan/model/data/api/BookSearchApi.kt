package liou.rayyuan.ebooksearchtaiwan.model.data.api

import io.ktor.client.*
import io.ktor.client.request.*
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkCrawerResult

class BookSearchApi(
    private val httpClient: HttpClient
) : BookSearchService {

    private val url = BuildConfig.HOST_URL + "searches" + "?q="

    override suspend fun postBooks(keyword: String): NetworkCrawerResult {
        return httpClient.post(url + keyword)
    }
}