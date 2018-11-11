package liou.rayyuan.ebooksearchtaiwan.model

sealed class HttpResult<out K: Any> {
    class Success<out K: Any>(val data: K): HttpResult<K>()
    class Error(val exception: Throwable): HttpResult<Nothing>()
}

