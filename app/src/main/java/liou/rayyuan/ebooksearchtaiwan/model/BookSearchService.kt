package liou.rayyuan.ebooksearchtaiwan.model

import kotlinx.coroutines.Deferred
import liou.rayyuan.ebooksearchtaiwan.model.entity.BookStores
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BookSearchService {

    @GET("search")
    fun getBooks(@Query("q") keyword: String): Deferred<Response<BookStores>>

}