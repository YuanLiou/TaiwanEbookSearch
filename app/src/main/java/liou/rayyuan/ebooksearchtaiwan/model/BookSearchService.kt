package liou.rayyuan.ebooksearchtaiwan.model

import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkBookStores
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BookSearchService {

    @GET("search")
    suspend fun getBooks(@Query("q") keyword: String): Response<NetworkBookStores>

}