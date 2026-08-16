package liou.rayyuan.ebooksearchtaiwan.appfunctions

import androidx.appfunctions.AppFunctionAppUnknownException
import androidx.appfunctions.AppFunctionElementNotFoundException
import androidx.appfunctions.AppFunctionException
import androidx.appfunctions.AppFunctionInvalidArgumentException
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.rayliu.commonmain.domain.search.BlankSearchIdException
import com.rayliu.commonmain.domain.search.BlankSearchQueryException
import com.rayliu.commonmain.domain.search.NetworkUnavailableException
import com.rayliu.commonmain.domain.search.SearchSnapshotNotFoundException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class AppFunctionErrorMapperInstrumentedTest {
    @Test
    fun blankQuery_mapsToInvalidArgument() {
        val mapped = AppFunctionErrorMapper.mapSearchFailure(BlankSearchQueryException())

        assertTrue(mapped is AppFunctionInvalidArgumentException)
    }

    @Test
    fun noNetwork_mapsToAppUnknown_withFixedMessage() {
        val mapped = AppFunctionErrorMapper.mapSearchFailure(NetworkUnavailableException())

        assertTrue(mapped is AppFunctionAppUnknownException)
        assertEquals(
            AppFunctionErrorMapper.NETWORK_UNAVAILABLE_MESSAGE,
            mapped.errorMessage
        )
    }

    @Test
    fun otherFailure_mapsToAppUnknown_withFixedMessage() {
        val mapped = AppFunctionErrorMapper.mapSearchFailure(IllegalStateException("failed"))

        assertTrue(mapped is AppFunctionAppUnknownException)
        assertEquals(AppFunctionErrorMapper.REQUEST_FAILED_MESSAGE, mapped.errorMessage)
    }

    @Test
    fun blankSearchId_mapsToInvalidArgument() {
        val mapped = AppFunctionErrorMapper.mapSnapshotFailure(BlankSearchIdException())

        assertTrue(mapped is AppFunctionInvalidArgumentException)
    }

    @Test
    fun snapshotNetworkFailure_mapsToAppUnknown_withFixedMessage() {
        val mapped = AppFunctionErrorMapper.mapSnapshotFailure(NetworkUnavailableException())

        assertTrue(mapped is AppFunctionAppUnknownException)
        assertEquals(
            AppFunctionErrorMapper.NETWORK_UNAVAILABLE_MESSAGE,
            mapped.errorMessage
        )
    }

    @Test
    fun snapshotNotFound_mapsToElementNotFound_withFixedMessage() {
        val mapped = AppFunctionErrorMapper.mapSnapshotFailure(SearchSnapshotNotFoundException())

        assertTrue(mapped is AppFunctionElementNotFoundException)
        assertEquals(AppFunctionErrorMapper.SNAPSHOT_NOT_FOUND_MESSAGE, mapped.errorMessage)
    }

    @Test
    fun snapshotOtherFailure_mapsToAppUnknown_withFixedMessage() {
        val mapped = AppFunctionErrorMapper.mapSnapshotFailure(IllegalStateException("failed"))

        assertTrue(mapped is AppFunctionAppUnknownException)
        assertEquals(AppFunctionErrorMapper.REQUEST_FAILED_MESSAGE, mapped.errorMessage)
    }

    @Test
    fun alreadyAppFunctionException_isUnchanged() {
        val exception: AppFunctionException = AppFunctionInvalidArgumentException("already")

        assertSame(exception, AppFunctionErrorMapper.mapSearchFailure(exception))
        assertSame(exception, AppFunctionErrorMapper.mapSnapshotFailure(exception))
    }
}
