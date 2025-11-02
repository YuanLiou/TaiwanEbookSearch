package com.rayliu.commonmain.domain.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class BrowseHistoryRepositoryImpl(
    private val userPreferences: DataStore<Preferences>
) : BrowseHistoryRepository {
    override suspend fun isUserSeenRankWindow(): Flow<Boolean> {
        val key = booleanPreferencesKey(KEY_USER_SEEN_RANK_WINDOW)
        return userPreferences.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preference ->
                preference[key] ?: false
            }
    }

    override suspend fun setUserHasSeenRankWindow() {
        val key = booleanPreferencesKey(KEY_USER_SEEN_RANK_WINDOW)
        userPreferences.edit { settings ->
            settings[key] = true
        }
    }

    companion object {
        private const val KEY_USER_SEEN_RANK_WINDOW = "key-user-seen-rank-window"
    }
}
