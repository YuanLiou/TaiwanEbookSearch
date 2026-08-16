package liou.rayyuan.ebooksearchtaiwan.appfunctions

import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunction
import androidx.appfunctions.AppFunctionAppUnknownException
import androidx.appfunctions.AppFunctionElementNotFoundException
import androidx.appfunctions.AppFunctionInvalidArgumentException
import androidx.appfunctions.AppFunctionService
import androidx.appfunctions.AppFunctionServiceEntryPoint
import com.rayliu.commonmain.OffsetDateTimeHelper
import com.rayliu.commonmain.domain.usecase.GetRecentSearchRecordsUseCase
import com.rayliu.commonmain.domain.usecase.GetSearchRecordsCountsUseCase
import com.rayliu.commonmain.domain.usecase.GetSlicedSearchSnapshotUseCase
import com.rayliu.commonmain.domain.usecase.SearchBooksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import liou.rayyuan.ebooksearchtaiwan.appfunctions.model.BookSearchResult
import liou.rayyuan.ebooksearchtaiwan.appfunctions.model.SearchRecordItem
import org.koin.android.ext.android.inject

@RequiresApi(36)
@AppFunctionServiceEntryPoint(
    serviceName = "EbookAppFunctionService",
    appFunctionXmlFileName = "ebook_app_function_service",
)
abstract class BaseEbookAppFunctionService : AppFunctionService() {
    private val getSearchRecordsCountsUseCase: GetSearchRecordsCountsUseCase by inject()
    private val getRecentSearchRecordsUseCase: GetRecentSearchRecordsUseCase by inject()
    private val offsetDateTimeHelper: OffsetDateTimeHelper by inject()
    private val searchBooksUseCase: SearchBooksUseCase by inject()
    private val getSlicedSearchSnapshotUseCase: GetSlicedSearchSnapshotUseCase by inject()

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

    /**
     * Read the newest local search strings from this device.
     *
     * Use this to resolve "the last book I searched" or similar follow-up.
     * These are personal search strings stored on the phone. This is not a
     * bookshelf and not a store catalog. The list is never the full history.
     *
     * @param limit Maximum number of records to return. Values of 0 or less
     *     become 10. Values above 20 become 20.
     * @return Newest first. Empty when this device has no usable history.
     *     lastSearchedAt is ISO-8601. SQL ids are not included.
     */
    @AppFunction(isDescribedByKDoc = true)
    internal suspend fun getRecentSearchRecords(limit: Int = 10): List<SearchRecordItem> =
        withContext(Dispatchers.IO) {
            getRecentSearchRecordsUseCase(limit).map { record ->
                RecentSearchRecordMapper.map(record, offsetDateTimeHelper)
            }
        }

    /**
     * Search Taiwan ebook stores by book title or ISBN and compare public prices.
     *
     * Call this for a live comparison. Repeat calls with the same query still
     * search again and return a result. This is not a bookshelf and not a
     * purchase.
     *
     * @param query Book title or ISBN. Blank or whitespace-only values fail.
     * @return Ranked store candidates. Prices stay in the store currency.
     *     Null price means unknown, not free. The cheapest bestResult is not
     *     guaranteed to be the same edition across stores.
     * @throws AppFunctionInvalidArgumentException If query is blank.
     * @throws AppFunctionAppUnknownException If the device is offline or the
     *     live search request fails. If offline, tell the user to reconnect.
     */
    @AppFunction(isDescribedByKDoc = true)
    internal suspend fun searchBooks(query: String): BookSearchResult =
        withContext(Dispatchers.IO) {
            val slice =
                searchBooksUseCase(query).getOrElse { error ->
                    throw AppFunctionErrorMapper.mapSearchFailure(error)
                }
            BookSearchResultMapper.map(slice, isSnapshot = false)
        }

    /**
     * Reload a saved Taiwan ebook price comparison by searchId.
     *
     * This function reads GET searches/{searchId}. It does not start a new
     * live search, so prices may be stale. Use searchBooks when the user
     * wants current prices.
     *
     * Required workflow: Call "searchBooks" first to obtain a searchId, unless
     * the user already provided a snapshot id.
     *
     * @param searchId Snapshot id from searchBooks. Blank values fail.
     * @return The saved comparison with isSnapshot true. Null price means
     *     unknown, not free.
     * @throws AppFunctionInvalidArgumentException If searchId is blank.
     * @throws AppFunctionElementNotFoundException If the snapshot does not
     *     exist or the snapshot request fails.
     * @throws AppFunctionAppUnknownException If the device is offline.
     *     Suggest checking the connection.
     */
    @AppFunction(isDescribedByKDoc = true)
    internal suspend fun getSearchSnapshot(searchId: String): BookSearchResult =
        withContext(Dispatchers.IO) {
            val slice =
                getSlicedSearchSnapshotUseCase(searchId).getOrElse { error ->
                    throw AppFunctionErrorMapper.mapSnapshotFailure(error)
                }
            BookSearchResultMapper.map(slice, isSnapshot = true)
        }
}
