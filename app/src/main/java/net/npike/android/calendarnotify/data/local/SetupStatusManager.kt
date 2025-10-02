package net.npike.android.calendarnotify.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetupStatusManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val IS_INITIAL_SETUP_COMPLETE = booleanPreferencesKey("is_initial_setup_complete")

    val isInitialSetupComplete: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_INITIAL_SETUP_COMPLETE] ?: false
        }

    suspend fun setInitialSetupComplete(isComplete: Boolean) {
        dataStore.edit {
            it[IS_INITIAL_SETUP_COMPLETE] = isComplete
        }
    }
}