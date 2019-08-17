package liou.rayyuan.ebooksearchtaiwan.model

import com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.model.entity.BookStores
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class APIManager {
    private val bookSearchService: BookSearchService
    private val timeout: Long = 30

    init {
        var userAgent = SystemInfoCollector.getUserAgent()
        val httpClientBuilder = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                        .header("User-Agent", userAgent)
                        .build()
                chain.proceed(request)
            }
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .callTimeout(timeout, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

        if (BuildConfig.DEBUG) {
            httpClientBuilder.addInterceptor(OkHttpProfilerInterceptor())
        }
        val httpClient = httpClientBuilder.build()

        val contentType = MediaType.get("application/json")
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.HOST_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .client(httpClient)
            .build()

        bookSearchService = retrofit.create(BookSearchService::class.java)
    }

    suspend fun getBooks(keywords: String): Response<BookStores> {
        return bookSearchService.getBooks(keywords)
    }
}