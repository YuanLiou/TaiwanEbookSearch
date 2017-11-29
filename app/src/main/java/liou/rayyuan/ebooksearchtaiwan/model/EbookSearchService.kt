package liou.rayyuan.ebooksearchtaiwan.model

import android.util.Log
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.HashMap
import javax.net.ssl.HttpsURLConnection

/**
 * Created by louis383 on 2017/11/19.
 */

class EbookSearchService {
    private val hostURL = BuildConfig.HOST_URL

    fun buildQueryStringURL(endpoint: String?, queryStrings: HashMap<String, String>): String? {
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

    fun connect(url: String, type: ConnectionType) : String? {
        val url = URL(url)
        with(url.openConnection() as HttpsURLConnection) {
            if (type.equals(ConnectionType.GET)) {
                requestMethod = "GET"
            } else if (type.equals(ConnectionType.POST)) {
                requestMethod = "POST"
            }
            connect()
            Log.i("Send to URL: ", url.toString())
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
                return buffer.toString()
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
                return buffer.toString()
            }

            return null
        }
    }
}
