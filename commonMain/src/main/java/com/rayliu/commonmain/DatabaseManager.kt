package com.rayliu.commonmain

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rayliu.commonmain.dao.SearchRecordDao
import com.rayliu.commonmain.entity.SearchRecord

@Database(entities = arrayOf(SearchRecord::class), version = 1)
@TypeConverters(OffsetDateTypeConverter::class)
abstract class DatabaseManager: RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "ebooktw_database"
    }

    abstract fun searchRecordDao(): SearchRecordDao

}