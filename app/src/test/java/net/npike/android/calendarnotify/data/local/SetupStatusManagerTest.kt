package net.npike.android.calendarnotify.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SetupStatusManagerTest {

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var setupStatusManager: SetupStatusManager

    @Before
    fun setup() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = {
                tmpFolder.preferencesDataStoreFile("test_setup_status_prefs")
            }
        )
        setupStatusManager = SetupStatusManager(dataStore)
    }

    @Test
    fun isInitialSetupComplete_defaultsToFalse() = testScope.runTest {
        assertFalse(setupStatusManager.isInitialSetupComplete.first())
    }

    @Test
    fun setInitialSetupComplete_setsToTrue() = testScope.runTest {
        setupStatusManager.setInitialSetupComplete(true)
        assertTrue(setupStatusManager.isInitialSetupComplete.first())
    }

    @Test
    fun setInitialSetupComplete_setsToFalse() = testScope.runTest {
        setupStatusManager.setInitialSetupComplete(true)
        assertTrue(setupStatusManager.isInitialSetupComplete.first())

        setupStatusManager.setInitialSetupComplete(false)
        assertFalse(setupStatusManager.isInitialSetupComplete.first())
    }
}
