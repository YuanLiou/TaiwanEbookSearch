package com.rayliu.commonmain.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.rayliu.commonmain.data.DataStoreFactory
import com.rayliu.commonmain.data.database.EbookTwDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<SqlDriver> {
        val driver = JdbcSqliteDriver("jdbc:sqlite:ebook_tw_database.db")
        EbookTwDatabase.Schema.create(driver)
        driver
    }

    single {
        DataStoreFactory()
    }
}
