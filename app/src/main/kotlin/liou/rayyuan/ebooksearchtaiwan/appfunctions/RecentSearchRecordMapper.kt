package liou.rayyuan.ebooksearchtaiwan.appfunctions

import com.rayliu.commonmain.OffsetDateTimeHelper
import com.rayliu.commonmain.domain.model.RecentSearchRecord
import liou.rayyuan.ebooksearchtaiwan.appfunctions.model.SearchRecordItem

object RecentSearchRecordMapper {
    fun map(
        record: RecentSearchRecord,
        offsetDateTimeHelper: OffsetDateTimeHelper
    ): SearchRecordItem =
        SearchRecordItem(
            query = record.query,
            lastSearchedAt = offsetDateTimeHelper.provideTimeStampString(record.lastSearchedAt),
            times = record.times
        )
}
