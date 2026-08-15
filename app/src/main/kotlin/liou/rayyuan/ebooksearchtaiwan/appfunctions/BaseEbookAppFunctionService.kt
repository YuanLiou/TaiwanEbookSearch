package liou.rayyuan.ebooksearchtaiwan.appfunctions

import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunction
import androidx.appfunctions.AppFunctionService
import androidx.appfunctions.AppFunctionServiceEntryPoint
import com.rayliu.commonmain.domain.usecase.GetSearchRecordsCountsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

@RequiresApi(36)
@AppFunctionServiceEntryPoint(
    serviceName = "EbookAppFunctionService",
    appFunctionXmlFileName = "ebook_app_function_service",
)
abstract class BaseEbookAppFunctionService : AppFunctionService() {
    private val getSearchRecordsCountsUseCase: GetSearchRecordsCountsUseCase by inject()

    /**
     * Temporarily verifies that Taiwan Ebook Search AppFunctions are registered.
     * This function is only a build and registration probe; it does not search books.
     *
     * @return A short confirmation that the AppFunctions integration is ready.
     */
    @AppFunction(isDescribedByKDoc = true)
    internal suspend fun pingAppFunctions(): String =
        withContext(Dispatchers.IO) {
            val recordCount = getSearchRecordsCountsUseCase().getOrDefault(0)
            "Taiwan Ebook Search AppFunctions ready. localRecordCount=$recordCount"
        }
}
