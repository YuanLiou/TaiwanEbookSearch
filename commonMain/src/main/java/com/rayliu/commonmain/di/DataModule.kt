package liou.rayyuan.ebooksearchtaiwan.di

import android.util.Log
import com.rayliu.commonmain.BuildConfig
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.observer.*
import io.ktor.client.request.*
import com.rayliu.commonmain.SystemInfoCollector
import com.rayliu.commonmain.data.api.BookSearchApi
import org.koin.dsl.module

private const val TIME_OUT = 60_000

val dataModule = module {

    // Provide: HttpClient
    single {
        HttpClient(Android) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(
                    kotlinx.serialization.json.Json {
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
