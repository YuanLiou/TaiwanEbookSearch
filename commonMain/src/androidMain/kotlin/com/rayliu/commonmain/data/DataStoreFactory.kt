package com.rayliu.commonmain.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = dataStoreFileName
)

actual class DataStoreFactory(
    private val context: Context
) {
    actual fun createDataStore(): DataStore<Preferences> = context.dataStore
}
