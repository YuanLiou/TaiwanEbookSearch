package liou.rayyuan.ebooksearchtaiwan.model

import android.os.AsyncTask

/**
 * Created by louis383 on 2017/11/19.
 */

class NetworkConnector(private val ebookSearchService: EbookSearchService, private val type: ConnectionType, private val listener: NetworkConnectionListener) : AsyncTask<String, Int, String>() {

    override fun doInBackground(vararg strings: String): String? {
        return ebookSearchService.connect(strings[0], type)
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        listener.onNetworkConnectionSucceed(result)
    }
}
