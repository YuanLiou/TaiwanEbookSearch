package liou.rayyuan.ebooksearchtaiwan.preferencesetting

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun SharedPreferences.booleanFlow(
    key: String,
    defaultValue: Boolean
) = callbackFlow {
    trySend(getBoolean(key, defaultValue))
    val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, changedKey ->
            if (changedKey == key) {
                trySend(sharedPreferences.getBoolean(key, defaultValue))
            }
        }
    registerOnSharedPreferenceChangeListener(listener)
    awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
}

fun SharedPreferences.stringFlow(
    key: String,
    defaultValue: String
) = callbackFlow {
    trySend(getString(key, defaultValue))
    val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, changedKey ->
            if (changedKey == key) {
                trySend(sharedPreferences.getString(key, defaultValue))
            }
        }
    registerOnSharedPreferenceChangeListener(listener)
    awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
}
