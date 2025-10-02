package net.npike.android.calendarnotify.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
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
    private val KEY_LAST_KNOWN_EVENT_ID = longPreferencesKey("last_known_event_id")

    val unmonitoredCalendarIds: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[KEY_UNMONITORED_CALENDAR_IDS] ?: emptySet()
        }

    suspend fun setUnmonitoredCalendarIds(ids: Set<String>) {
        dataStore.edit { settings ->
            settings[KEY_UNMONITORED_CALENDAR_IDS] = ids
        }
    }

    val lastKnownEventId: Flow<Long> = dataStore.data
        .map { preferences ->
            preferences[KEY_LAST_KNOWN_EVENT_ID] ?: 0L
        }

    suspend fun setLastKnownEventId(eventId: Long) {
        dataStore.edit { settings ->
            settings[KEY_LAST_KNOWN_EVENT_ID] = eventId
        }
    }
}