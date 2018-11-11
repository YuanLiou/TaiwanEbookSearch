package liou.rayyuan.ebooksearchtaiwan.model

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import retrofit2.Response
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class NetworkLiveData<T>(private val call: Deferred<Response<T>>): LiveData<T>() {
    companion object {
        const val GENERIC_NETWORK_ISSUE = "generic-network-issue"
    }

    private val tag: String = "NetworkLiveData"
    private var requestJob: Job? = null
    var listener: OnNetworkConnectionListener? = null

    fun requestData() {
        requestJob?.takeIf { it.isCancelled || it.isCompleted }?.run { return }
        requestJob = CoroutineScope(Dispatchers.IO).launch {
            val result = getData()
            withContext(Dispatchers.Main) {
                when (result) {
                    is HttpResult.Success -> {
                        val isSuccessful = result.data.isSuccessful
                        if (isSuccessful) {
                            value = result.data.body()
                        } else {
                            listener?.onNetworkErrorOccurred(result.data.errorBody())
                        }
                    }
                    is HttpResult.Error -> {
                        Log.e(tag, Log.getStackTraceString(result.exception))
                        if (result.exception is SocketTimeoutException ||
                                result.exception is TimeoutException) {
                            listener?.onNetworkTimeout()
                        } else {
                            val message = result.exception.localizedMessage
                                    ?: GENERIC_NETWORK_ISSUE
                            listener?.onExceptionOccurred(message)
                        }
                    }
                }
            }
        }
    }

    fun cancel() {
        requestJob?.takeIf { it.isActive }?.run {
            cancel()
            Log.i(tag, "request is canceled.")
        }
    }

    fun isConnecting(): Boolean {
        return requestJob?.isActive ?: false
    }

    private suspend fun getData(): HttpResult<Response<T>> {
        return try {
            val result = call.await()
            HttpResult.Success(result)
        } catch (e: Throwable) {
            HttpResult.Error(e)
        }
    }
}