package liou.rayyuan.ebooksearchtaiwan.model

import okhttp3.ResponseBody

interface OnNetworkConnectionListener<T> {
    fun onNetworkRequestSuccess(t: T)
    fun onNetworkErrorOccurred(errorBody: ResponseBody?)
    fun onExceptionOccurred(message: String)
    fun onNetworkTimeout()
}