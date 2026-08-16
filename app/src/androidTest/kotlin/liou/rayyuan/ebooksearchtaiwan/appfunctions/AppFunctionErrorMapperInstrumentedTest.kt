package liou.rayyuan.ebooksearchtaiwan.appfunctions

import androidx.appfunctions.AppFunctionAppUnknownException
import androidx.appfunctions.AppFunctionException
import androidx.appfunctions.AppFunctionInvalidArgumentException
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.rayliu.commonmain.domain.search.BlankSearchQueryException
import com.rayliu.commonmain.domain.search.NetworkUnavailableException
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
    fun otherFailure_mapsToAppUnknown() {
        val mapped = AppFunctionErrorMapper.mapSearchFailure(IllegalStateException("failed"))

        assertTrue(mapped is AppFunctionAppUnknownException)
        assertEquals("failed", mapped.errorMessage)
    }

    @Test
    fun alreadyAppFunctionException_isUnchanged() {
        val exception: AppFunctionException = AppFunctionInvalidArgumentException("already")

        assertSame(exception, AppFunctionErrorMapper.mapSearchFailure(exception))
    }
}
