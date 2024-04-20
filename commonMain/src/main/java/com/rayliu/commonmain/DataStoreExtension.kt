package com.rayliu.commonmain

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.rayliu.commonmain.domain.service.UserPreferenceManager

private const val DATA_STORE_NAME = "UserDataStore"

internal val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
    DATA_STORE_NAME,
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration(context = context, sharedPreferencesName = UserPreferenceManager.USER_PREFERENCE_NAME)
        )
    }
)
