package com.rayliu.commonmain.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File

actual class DataStoreFactory {
    actual fun createDataStore(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = {
                File(System.getProperty("java.io.tmpdir"), dataStoreFileName)
            }
        )
    }
}
