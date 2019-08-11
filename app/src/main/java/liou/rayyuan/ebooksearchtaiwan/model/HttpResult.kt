package liou.rayyuan.ebooksearchtaiwan.model

import okhttp3.ResponseBody

sealed class HttpResult<out K: Any> {
    class Success<out K: Any>(val data: K): HttpResult<K>()
    class Error(val errorBody: ResponseBody?, val useGenericError: Boolean = false): HttpResult<Nothing>()
    class ErrorInException(val exception: Throwable): HttpResult<Nothing>()
    object Empty: HttpResult<Nothing>()
}

