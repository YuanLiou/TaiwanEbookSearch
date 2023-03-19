package com.rayliu.commonmain.di

import android.util.Log
import com.rayliu.commonmain.BuildConfig
import com.rayliu.commonmain.SystemInfoCollector
import com.rayliu.commonmain.data.api.BookSearchApi
import com.rayliu.commonmain.data.api.BookSearchService
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

private const val apiVersion = "v1"

val dataModule = module {

    // Provide: HttpClient
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }

            engine {
                maxConnectionsCount = 30
                endpoint {
                    maxConnectionsPerRoute = 100
                    pipelineMaxSize = 20
                    keepAliveTime = 5000
                    connectTimeout = 5000
                    connectAttempts = 5
                }
            }

            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = BuildConfig.HOST_URL
                    if (BuildConfig.DEBUG) {
                        port = BuildConfig.HOST_PORT
                    }
                    path("$apiVersion/")
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
    factory<BookSearchService> {
        BookSearchApi(get<HttpClient>())
    }
}
