package net.npike.android.calendarnotify.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val KEY_UNMONITORED_CALENDAR_IDS = stringSetPreferencesKey("unmonitored_calendar_ids")

    val unmonitoredCalendarIds: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[KEY_UNMONITORED_CALENDAR_IDS] ?: emptySet()
        }

    suspend fun setUnmonitoredCalendarIds(ids: Set<String>) {
        dataStore.edit { settings ->
            settings[KEY_UNMONITORED_CALENDAR_IDS] = ids
        }
    }
}