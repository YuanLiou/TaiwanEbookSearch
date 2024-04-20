package com.rayliu.commonmain.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rayliu.commonmain.data.dto.LocalSearchRecord
import org.threeten.bp.OffsetDateTime

@Dao
interface SearchRecordDao {
    @Query("SELECT * FROM search_records ORDER BY datetime(time_stamp) DESC")
    fun getAllSearchRecords(): LiveData<List<LocalSearchRecord>>

    @Query("SELECT * FROM search_records ORDER BY datetime(time_stamp) DESC")
    fun getSearchRecordsPaged(): DataSource.Factory<Int, LocalSearchRecord>

    @Query(
        """
        SELECT * FROM search_records
        WHERE result_text = :passedRecord
        LIMIT 1
        """
    )
    fun getSearchRecordWithTitle(passedRecord: String): LocalSearchRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecords(searchRecords: List<LocalSearchRecord>)

    @Delete
    fun deleteRecord(searchRecord: LocalSearchRecord)

    @Update
    fun updateRecord(searchRecord: LocalSearchRecord)

    @Query("SELECT count(*) FROM search_records")
    fun getSearchRecordsCounts(): Int

    @Query(
        """
        UPDATE search_records SET time_stamp = :timeStamp, counts = :counts
        WHERE id = :id
    """
    )
    fun updateCounts(
        id: Int,
        counts: Int,
        timeStamp: OffsetDateTime
    )

    @Query("DELETE FROM search_records")
    fun deleteAllRecords()
}
