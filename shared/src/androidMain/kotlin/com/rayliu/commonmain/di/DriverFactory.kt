package com.rayliu.commonmain.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.rayliu.commonmain.data.database.EbookTwDatabase

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(EbookTwDatabase.Schema, context, "ebooktw_database.db")
    }
}
