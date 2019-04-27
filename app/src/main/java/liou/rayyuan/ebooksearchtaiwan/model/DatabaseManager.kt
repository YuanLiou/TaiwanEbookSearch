package liou.rayyuan.ebooksearchtaiwan.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import liou.rayyuan.ebooksearchtaiwan.model.dao.SearchRecordDao
import liou.rayyuan.ebooksearchtaiwan.model.entity.SearchRecord

@Database(entities = arrayOf(SearchRecord::class), version = 1)
@TypeConverters(OffsetDateTypeConverter::class)
abstract class DatabaseManager: RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "ebooktw_database"
    }

    abstract fun searchRecordDao(): SearchRecordDao

}