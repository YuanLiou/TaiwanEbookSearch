package com.rayliu.commonmain.domain

sealed class TaskResult<out T, out E> {
    data class Success<out T>(val value: T) : TaskResult<T, Nothing>()
    data class Failed<out E>(val error: E) : TaskResult<Nothing, E>()
    inline fun <C> fold(success: (T) -> C, failure: (E) -> C): C = when (this) {
        is Success -> success(value)
        is Failed -> failure(error)
    }
}

typealias SimpleResult<T> = TaskResult<T, Throwable>