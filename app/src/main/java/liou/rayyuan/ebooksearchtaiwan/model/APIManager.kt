package liou.rayyuan.ebooksearchtaiwan.model

import com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.model.entity.BookStores
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.HOST_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(httpClient)
            .build()

        bookSearchService = retrofit.create(BookSearchService::class.java)
    }

    fun getBooks(keywords: String): NetworkRequest<BookStores> {
        return NetworkRequest(bookSearchService.getBooks(keywords))
    }
}