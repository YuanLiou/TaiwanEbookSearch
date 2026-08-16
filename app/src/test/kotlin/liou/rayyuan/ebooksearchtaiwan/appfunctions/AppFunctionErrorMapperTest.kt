package liou.rayyuan.ebooksearchtaiwan.appfunctions

import com.rayliu.commonmain.domain.search.BlankSearchQueryException
import com.rayliu.commonmain.domain.search.NetworkUnavailableException
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
    fun otherFailure_mapsToUnknown() {
        assertEquals(
            SearchFailureKind.UNKNOWN,
            AppFunctionErrorMapper.classifySearchFailure(IllegalStateException("failed"))
        )
    }
}
