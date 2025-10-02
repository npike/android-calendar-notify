package net.npike.android.calendarnotify.data.repository

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.MatrixCursor
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import net.npike.android.calendarnotify.data.local.DataStoreManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalendarRepositoryTest {

    private lateinit var repository: CalendarRepository
    private val mockContentResolver: ContentResolver = mockk(relaxed = true)
    private val mockDataStoreManager: DataStoreManager = mockk(relaxed = true)
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setup() {
        mockkStatic(ContextCompat::class)
        every {
            ContextCompat.checkSelfPermission(
                any(),
                any()
            )
        } returns PackageManager.PERMISSION_GRANTED

        repository = CalendarRepository(
            context = context,
            contentResolver = mockContentResolver,
            dataStoreManager = mockDataStoreManager
        )
    }

    @Test
    fun `getSystemCalendars applies unmonitored IDs from DataStore`() = runBlocking {
        // Arrange
        val calendarsCursor = MatrixCursor(
            arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR,
                CalendarContract.Calendars.IS_PRIMARY,
                CalendarContract.Calendars.SYNC_EVENTS
            )
        ).apply {
            addRow(arrayOf("1", "Personal", -1, 1, 1)) // Added 1 for SYNC_EVENTS
            addRow(arrayOf("2", "Work", -1, 0, 1)) // Added 1 for SYNC_EVENTS
        }
        every {
            mockContentResolver.query(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns calendarsCursor
        coEvery { mockDataStoreManager.unmonitoredCalendarIds } returns flowOf(setOf("2"))

        // Act
        val calendars = repository.getSystemCalendars().first()

        // Assert
        assertEquals(2, calendars.size)
        assertEquals(true, calendars.find { it.id == "1" }?.isMonitored)
        assertEquals(false, calendars.find { it.id == "2" }?.isMonitored)
    }

    @Test
    fun `getHighestEventId returns the highest event ID`() = runBlocking {
        // Arrange
        val eventsCursor = MatrixCursor(arrayOf(CalendarContract.Events._ID)).apply {
            addRow(arrayOf(100L))
            addRow(arrayOf(99L))
        }
        every { mockContentResolver.query(any(), any(), any(), any(), any()) } returns eventsCursor

        // Act
        val highestId = repository.getHighestEventId()

        // Assert
        assertEquals(100L, highestId)
    }

    @Test
    fun `getEventsFromCalendarProviderSinceEventId queries for events with ID greater than lastEventId`() =
        runBlocking {
            // Arrange
            val eventsCursor = MatrixCursor(
                arrayOf(
                    CalendarContract.Events._ID,
                    CalendarContract.Events.TITLE,
                    CalendarContract.Events.DTSTART,
                    CalendarContract.Events.DTEND,
                    CalendarContract.Events.CALENDAR_ID,
                    CalendarContract.Events.EVENT_LOCATION,
                    CalendarContract.Events.ALL_DAY,
                    CalendarContract.Events.LAST_DATE
                )
            ).apply {
                addRow(arrayOf(101L, "New Event", 0L, 0L, "1", "Location", 0, 0L))
            }
            every {
                mockContentResolver.query(
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns eventsCursor

            // Act
            val events = repository.getEventsFromCalendarProviderSinceEventId("1", 100L)

            // Assert
            assertEquals(1, events.size)
            assertEquals("101", events.first().id)
        }
}
