package net.npike.android.calendarnotify.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val KEY_FIRST_RUN_TIMESTAMP = longPreferencesKey("first_run_timestamp")

    suspend fun getFirstRunTimestamp(): Long {
        return dataStore.data.map { preferences ->
            preferences[KEY_FIRST_RUN_TIMESTAMP] ?: 0L
        }.first()
    }

    suspend fun setFirstRunTimestamp(timestamp: Long) {
        dataStore.edit { settings ->
            settings[KEY_FIRST_RUN_TIMESTAMP] = timestamp
        }
    }
}