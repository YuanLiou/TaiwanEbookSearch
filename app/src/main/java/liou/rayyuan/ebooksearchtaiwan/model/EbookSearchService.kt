package liou.rayyuan.ebooksearchtaiwan.model

import android.util.Log
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.SocketTimeoutException
import java.net.URL
import java.util.HashMap
import java.util.concurrent.TimeoutException
import javax.net.ssl.HttpsURLConnection

/**
 * Created by louis383 on 2017/11/19.
 */

class EbookSearchService {

    companion object {
        val timeout: Int = -1
        val succeed: Int = 200
        val error: Int = 400
    }

    private val hostURL = BuildConfig.HOST_URL

    fun getBooksInfo(queryStrings: HashMap<String, String>): String? {
        return buildQueryStringURL("search", queryStrings)
    }

    private fun buildQueryStringURL(endpoint: String?, queryStrings: HashMap<String, String>): String? {
        if (endpoint != null) {
            var endpointURL: String = hostURL
            endpointURL += endpoint + '?'

            var index = 0;
            for (entry in queryStrings.entries) {
                if (index != 0) {
                    endpointURL += '&'
                }
                endpointURL += entry.key + "=" + entry.value
                index++
            }
            Log.i("URL is = ", endpointURL)
            return endpointURL
        }
        return null
    }

    fun connect(url: String, type: ConnectionType) : Pair<String?, Int>? {
        val targetUrl = URL(url)
        try {
            with(targetUrl.openConnection() as HttpsURLConnection) {
                if (type.equals(ConnectionType.GET)) {
                    requestMethod = "GET"
                } else if (type.equals(ConnectionType.POST)) {
                    requestMethod = "POST"
                }
                connect()
                Log.i("Send to URL: ", targetUrl.toString())
                Log.i("Response Code", responseCode.toString())

                if (responseCode == 200) {
                    if (inputStream == null) {
                        return null
                    }
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val buffer = StringBuffer()
                    buffer.append(reader.readText())
                    if (buffer.isEmpty()) {
                        return null
                    }
                    reader.close()
                    return Pair(buffer.toString(), responseCode)
                } else if (responseCode >= 400) {
                    if (errorStream == null) {
                        return null
                    }
                    val reader = BufferedReader(InputStreamReader(errorStream))
                    val buffer = StringBuffer()
                    buffer.append(reader.readText())
                    if (buffer.isEmpty()) {
                        return null
                    }
                    reader.close()
                    return Pair(buffer.toString(), responseCode)
                }
            }
        } catch (exception: SocketTimeoutException) {
            Log.e("EbookSearchService", "SocketTimeoutException")
            Log.e("EbookSearchService", Log.getStackTraceString(exception))
            return Pair(null, timeout)
        } catch (exception: TimeoutException) {
            Log.e("EbookSearchService", "SocketTimeoutException")
            Log.e("EbookSearchService", Log.getStackTraceString(exception))
            return Pair(null, timeout)
        }
        return null
    }
}
