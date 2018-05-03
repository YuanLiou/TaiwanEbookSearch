package liou.rayyuan.ebooksearchtaiwan.model

import okhttp3.ResponseBody

interface OnNetworkConnectionListener {
    fun onNetworkErrorOccurred(errorBody: ResponseBody?)
    fun onNetworkTimeout()
}