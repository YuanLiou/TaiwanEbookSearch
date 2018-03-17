package liou.rayyuan.ebooksearchtaiwan.model

import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.model.entity.BookStores
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class APIManager {

    private val bookSearchService: BookSearchService
    private val timeout: Long = 60

    init {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
        if (BuildConfig.DEBUG) {
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.HOST_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        bookSearchService = retrofit.create(BookSearchService::class.java)
    }

    fun getBooks(keywords: String): NetworkLiveData<BookStores> {
        return NetworkLiveData(bookSearchService.getBooks(keywords))
    }

}