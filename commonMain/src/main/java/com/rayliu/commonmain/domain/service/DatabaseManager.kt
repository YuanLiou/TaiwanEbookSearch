package com.rayliu.commonmain.domain.service

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rayliu.commonmain.OffsetDateTypeConverter
import com.rayliu.commonmain.data.dao.SearchRecordDao
import com.rayliu.commonmain.data.dto.LocalSearchRecord

@Database(entities = arrayOf(LocalSearchRecord::class), version = 1)
@TypeConverters(OffsetDateTypeConverter::class)
abstract class DatabaseManager: RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "ebooktw_database"
    }

    abstract fun searchRecordDao(): SearchRecordDao
}