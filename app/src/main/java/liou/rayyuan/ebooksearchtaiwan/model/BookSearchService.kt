package liou.rayyuan.ebooksearchtaiwan.model

import liou.rayyuan.ebooksearchtaiwan.model.entity.BookStores
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BookSearchService {

    @GET("search")
    fun getBooks(@Query("q") keyword: String): Call<BookStores>

}