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
