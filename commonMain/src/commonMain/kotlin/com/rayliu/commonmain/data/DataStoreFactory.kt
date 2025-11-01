package com.rayliu.commonmain.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

internal const val dataStoreFileName = "user_prefs.preferences_pb"

expect class DataStoreFactory {
    fun createDataStore(): DataStore<Preferences>
}
