package liou.rayyuan.ebooksearchtaiwan.appfunctions

import com.rayliu.commonmain.domain.search.BlankSearchIdException
import com.rayliu.commonmain.domain.search.BlankSearchQueryException
import com.rayliu.commonmain.domain.search.NetworkUnavailableException
import com.rayliu.commonmain.domain.search.SearchSnapshotNotFoundException
import org.junit.Assert.assertEquals
import org.junit.Test

class AppFunctionErrorMapperTest {
    @Test
    fun blankQuery_mapsToInvalidArgument() {
        assertEquals(
            SearchFailureKind.INVALID_ARGUMENT,
            AppFunctionErrorMapper.classifySearchFailure(BlankSearchQueryException())
        )
    }

    @Test
    fun noNetwork_mapsToNetworkUnavailable() {
        assertEquals(
            SearchFailureKind.NETWORK_UNAVAILABLE,
            AppFunctionErrorMapper.classifySearchFailure(NetworkUnavailableException())
        )
    }

    @Test
    fun blankSearchId_mapsToInvalidArgument() {
        assertEquals(
            SnapshotFailureKind.INVALID_ARGUMENT,
            AppFunctionErrorMapper.classifySnapshotFailure(BlankSearchIdException())
        )
    }

    @Test
    fun snapshotNetworkFailure_mapsToNetworkUnavailable() {
        assertEquals(
            SnapshotFailureKind.NETWORK_UNAVAILABLE,
            AppFunctionErrorMapper.classifySnapshotFailure(NetworkUnavailableException())
        )
    }

    @Test
    fun snapshotNotFound_mapsToNotFound() {
        assertEquals(
            SnapshotFailureKind.NOT_FOUND,
            AppFunctionErrorMapper.classifySnapshotFailure(SearchSnapshotNotFoundException())
        )
    }

    @Test
    fun snapshotOtherFailure_mapsToUnknown() {
        assertEquals(
            SnapshotFailureKind.UNKNOWN,
            AppFunctionErrorMapper.classifySnapshotFailure(IllegalStateException("failed"))
        )
    }

    @Test
    fun otherFailure_mapsToUnknown() {
        assertEquals(
            SearchFailureKind.UNKNOWN,
            AppFunctionErrorMapper.classifySearchFailure(IllegalStateException("failed"))
        )
    }
}
