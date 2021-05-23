package liou.rayyuan.ebooksearchtaiwan.model

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import kotlinx.serialization.json.Json
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkBookStore
import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkCrawerResult
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class APIManager {
    private val bookSearchService: BookSearchService
    private val timeout: Long = 30

    init {
        val userAgent = SystemInfoCollector.getUserAgent()
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

        val contentType = "application/json".toMediaType()
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.HOST_URL)
            .addConverterFactory(Json {
                ignoreUnknownKeys = true
            }.asConverterFactory(contentType))
            .client(httpClient)
            .build()

        bookSearchService = retrofit.create(BookSearchService::class.java)
    }

    suspend fun postBooks(keywords: String): Response<NetworkCrawerResult> {
        return bookSearchService.postBooks(keywords)
    }
}