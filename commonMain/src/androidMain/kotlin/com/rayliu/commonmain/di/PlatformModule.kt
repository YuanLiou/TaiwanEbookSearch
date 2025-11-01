package com.rayliu.commonmain.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.rayliu.commonmain.data.DataStoreFactory
import com.rayliu.commonmain.data.database.EbookTwDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

private const val DATABASE_NAME = "ebooktw_database"

actual val platformModule: Module = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = EbookTwDatabase.Schema,
            context = androidContext(),
            name = DATABASE_NAME
        )
    }

    single {
        DataStoreFactory(androidContext())
    }
}
