package liou.rayyuan.ebooksearchtaiwan.model.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import liou.rayyuan.ebooksearchtaiwan.model.entity.SearchRecord
import org.threeten.bp.OffsetDateTime

@Dao
interface SearchRecordDao {

    @Query("SELECT * FROM search_records ORDER BY datetime(time_stamp)")
    fun getAllSearchRecords(): LiveData<List<SearchRecord>>

    @Query("SELECT * FROM search_records ORDER BY datetime(time_stamp)")
    fun getSearchRecordsPaged(): DataSource.Factory<Int, SearchRecord>

    @Query("""
        SELECT * FROM search_records
        WHERE result_text = :passedRecord
        """)
    fun getSearchRecordWithTitle(passedRecord: String): SearchRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecords(searchRecords: List<SearchRecord>)

    @Delete
    fun deleteRecord(searchRecord: SearchRecord)

    @Update
    fun updateRecord(searchRecord: SearchRecord)

    @Query("SELECT count(*) FROM search_records")
    fun getSearchRecordsCounts(): Int

    @Query("""
        UPDATE search_records SET time_stamp = :timeStamp, counts = :counts
        WHERE id = :id
    """)
    fun updateCounts(id: Int, counts: Int, timeStamp: OffsetDateTime)

}