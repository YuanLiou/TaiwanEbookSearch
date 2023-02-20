package liou.rayyuan.ebooksearchtaiwan.di

import android.util.Log
import com.rayliu.commonmain.BuildConfig
import com.rayliu.commonmain.SystemInfoCollector
import com.rayliu.commonmain.data.api.BookSearchApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

private const val TIME_OUT = 60_000

val dataModule = module {

    // Provide: HttpClient
    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
                engine {
                    connectTimeout = TIME_OUT
                    socketTimeout = TIME_OUT
                }
            }

            install(DefaultRequest) {
                header("User-Agent", SystemInfoCollector.getUserAgent())
            }

            if (BuildConfig.DEBUG) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            Log.v("Logger Ktor ->", message)
                        }
                    }
                    level = LogLevel.ALL
                }

                install(ResponseObserver) {
                    onResponse { response ->
                        Log.d("Http status:", "${response.status.value}")
                    }
                }
            }
        }
    }

    // Provide: BookSearchService
    factory {
        BookSearchApi(get<HttpClient>())
    }
}
