package net.npike.android.calendarnotify.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SetupStatusManagerTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var setupStatusManager: SetupStatusManager

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { context.preferencesDataStoreFile("test_setup_status_prefs") }
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