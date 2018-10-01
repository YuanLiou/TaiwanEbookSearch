package liou.rayyuan.ebooksearchtaiwan.model

import android.util.Log
import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class NetworkLiveData<T>(private val call: Call<T>): LiveData<T>(), Callback<T> {
    companion object {
        const val GENERIC_NETWORK_ISSUE = "generic-network-issue"
    }

    private val tag: String = "NetworkLiveData"
    var listener: OnNetworkConnectionListener? = null

    //region Retrofit.Callback<T>
    override fun onFailure(call: Call<T>?, t: Throwable?) {
        Log.e(tag, Log.getStackTraceString(t))
        if (t is SocketTimeoutException || t is TimeoutException) {
            listener?.onNetworkTimeout()
        } else {
            val message = t?.localizedMessage ?: GENERIC_NETWORK_ISSUE
            listener?.onExceptionOccurred(message)
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