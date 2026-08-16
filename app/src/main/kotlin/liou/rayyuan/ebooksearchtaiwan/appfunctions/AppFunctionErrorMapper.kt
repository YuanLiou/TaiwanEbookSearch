package liou.rayyuan.ebooksearchtaiwan.appfunctions

import androidx.appfunctions.AppFunctionAppUnknownException
import androidx.appfunctions.AppFunctionElementNotFoundException
import androidx.appfunctions.AppFunctionException
import androidx.appfunctions.AppFunctionInvalidArgumentException
import com.rayliu.commonmain.domain.search.BlankSearchIdException
import com.rayliu.commonmain.domain.search.BlankSearchQueryException
import com.rayliu.commonmain.domain.search.NetworkUnavailableException

internal enum class SearchFailureKind {
    ALREADY_MAPPED,
    INVALID_ARGUMENT,
    NETWORK_UNAVAILABLE,
    UNKNOWN
}

internal enum class SnapshotFailureKind {
    ALREADY_MAPPED,
    INVALID_ARGUMENT,
    NETWORK_UNAVAILABLE,
    NOT_FOUND
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

    internal fun classifySnapshotFailure(error: Throwable): SnapshotFailureKind =
        when (error) {
            is AppFunctionException -> SnapshotFailureKind.ALREADY_MAPPED
            is BlankSearchIdException -> SnapshotFailureKind.INVALID_ARGUMENT
            is NetworkUnavailableException -> SnapshotFailureKind.NETWORK_UNAVAILABLE
            else -> SnapshotFailureKind.NOT_FOUND
        }

    fun mapSnapshotFailure(error: Throwable): AppFunctionException =
        when (classifySnapshotFailure(error)) {
            SnapshotFailureKind.ALREADY_MAPPED -> {
                error as AppFunctionException
            }

            SnapshotFailureKind.INVALID_ARGUMENT -> {
                AppFunctionInvalidArgumentException(
                    "searchId must be the snapshot id returned by searchBooks."
                )
            }

            SnapshotFailureKind.NETWORK_UNAVAILABLE -> {
                AppFunctionAppUnknownException(NETWORK_UNAVAILABLE_MESSAGE)
            }

            SnapshotFailureKind.NOT_FOUND -> {
                AppFunctionElementNotFoundException(
                    error.message ?: "Search snapshot was not found."
                )
            }
        }
}
