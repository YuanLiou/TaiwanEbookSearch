package liou.rayyuan.ebooksearchtaiwan.model

import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkBookStore
import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkCrawerResult
import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkResult
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface BookSearchService {

    @POST("searches")
    suspend fun postBooks(@Query("q", encoded = true) keyword: String): Response<NetworkCrawerResult>

}