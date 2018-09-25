package liou.rayyuan.ebooksearchtaiwan.model

import androidx.lifecycle.LiveData
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class NetworkLiveData<T>(private val call: Call<T>): LiveData<T>(), Callback<T> {

    private val tag: String = "NetworkLiveData"
    var listener: OnNetworkConnectionListener? = null

    //region Retrofit.Callback<T>
    override fun onFailure(call: Call<T>?, t: Throwable?) {
        Log.e(tag, Log.getStackTraceString(t))
        if (t is SocketTimeoutException || t is TimeoutException) {
            listener?.onNetworkTimeout()
        }
    }

    override fun onResponse(call: Call<T>?, response: Response<T>?) {
        val isSuccessful = response?.isSuccessful ?: false
        if (isSuccessful) {
            value = response?.body()
        } else {
            listener?.onNetworkErrorOccurred(response?.errorBody())
        }
    }
    //endregion

    fun requestData() {
        if (!call.isCanceled && !call.isExecuted) {
            call.enqueue(this)
        }
    }

    fun cancel() {
        if (!call.isCanceled) {
            call.cancel()
            Log.i("NetworkLiveData", "request is canceled.")
        }
    }

    fun isConnecting(): Boolean {
        return call.isExecuted
    }
}