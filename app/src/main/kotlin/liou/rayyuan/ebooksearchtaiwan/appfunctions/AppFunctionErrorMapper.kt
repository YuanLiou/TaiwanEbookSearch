package liou.rayyuan.ebooksearchtaiwan.appfunctions

import androidx.appfunctions.AppFunctionAppUnknownException
import androidx.appfunctions.AppFunctionException
import androidx.appfunctions.AppFunctionInvalidArgumentException
import com.rayliu.commonmain.domain.search.BlankSearchQueryException
import com.rayliu.commonmain.domain.search.NetworkUnavailableException

internal enum class SearchFailureKind {
    ALREADY_MAPPED,
    INVALID_ARGUMENT,
    NETWORK_UNAVAILABLE,
    UNKNOWN
}

object AppFunctionErrorMapper {
    const val NETWORK_UNAVAILABLE_MESSAGE =
        "Network is unavailable. Check the connection and retry."

    internal fun classifySearchFailure(error: Throwable): SearchFailureKind =
        when (error) {
            is AppFunctionException -> SearchFailureKind.ALREADY_MAPPED
            is BlankSearchQueryException -> SearchFailureKind.INVALID_ARGUMENT
            is NetworkUnavailableException -> SearchFailureKind.NETWORK_UNAVAILABLE
            else -> SearchFailureKind.UNKNOWN
        }

    fun mapSearchFailure(error: Throwable): AppFunctionException =
        when (classifySearchFailure(error)) {
            SearchFailureKind.ALREADY_MAPPED -> {
                error as AppFunctionException
            }

            SearchFailureKind.INVALID_ARGUMENT -> {
                AppFunctionInvalidArgumentException(
                    "query must be a book title or ISBN and must not be blank."
                )
            }

            SearchFailureKind.NETWORK_UNAVAILABLE -> {
                AppFunctionAppUnknownException(NETWORK_UNAVAILABLE_MESSAGE)
            }

            SearchFailureKind.UNKNOWN -> {
                AppFunctionAppUnknownException(error.message ?: "Book search failed.")
            }
        }
}
