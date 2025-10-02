package net.npike.android.calendarnotify.data.repository

import android.content.Context
import android.content.ContentResolver
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import net.npike.android.calendarnotify.data.local.CalendarDao
import net.npike.android.calendarnotify.data.local.CalendarEntity
import net.npike.android.calendarnotify.data.local.EventDao
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class CalendarRepositoryTest {

    private val mockContext = mockk<Context>()
    private val mockContentResolver = mockk<ContentResolver>()
    private val mockCalendarDao = mockk<CalendarDao>()
    private val mockEventDao = mockk<EventDao>()
    private lateinit var repository: CalendarRepository

    @Before
    fun setup() {
        clearAllMocks()
        // Mock the registerContentObserver call to prevent ContentObserver from being instantiated
        every { mockContentResolver.registerContentObserver(any(), any(), any()) } just Runs
        repository = CalendarRepository(mockContext, mockContentResolver, mockCalendarDao, mockEventDao)
    }

    @Test
    fun `fetchAndStoreSystemCalendars inserts new calendars`() = runBlocking {
        val systemCalendars = listOf(
            CalendarEntity("1", "Work", 0xFF0000, true),
            CalendarEntity("2", "Personal", 0x00FF00, true)
        )

        coEvery { mockCalendarDao.getCalendarById(any<String>()) } returns null
        coEvery { mockCalendarDao.insertAll(any()) } just Runs

        // Mock cursor for contentResolver.query
        val mockCursor = mockk<android.database.Cursor>()
        every { mockCursor.moveToNext() } returnsMany listOf(true, true, false)
        every { mockCursor.getString(0) } returns "1" andThen "2"
        every { mockCursor.getString(1) } returns "Work" andThen "Personal"
        every { mockCursor.getInt(2) } returns 0xFF0000 andThen 0x00FF00 // Mock CALENDAR_COLOR
        every { mockCursor.getInt(3) } returns 0 andThen 0 // Mock IS_PRIMARY
        every { mockCursor.getColumnIndexOrThrow(any()) } answers {
            when (arg<String>(0)) {
                CalendarContract.Calendars._ID -> 0
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME -> 1
                CalendarContract.Calendars.CALENDAR_COLOR -> 2
                CalendarContract.Calendars.IS_PRIMARY -> 3
                else -> throw IllegalArgumentException("Unknown column")
            }
        }
        every { mockCursor.close() } just Runs

        every { mockContentResolver.query(any(), any(), any(), any(), any()) } returns mockCursor

        repository.fetchAndStoreSystemCalendars()

        coVerify { mockCalendarDao.insertAll(match { it.size == 2 }) }
    }

    @Test
    fun `updateCalendarMonitoring updates calendar in database`() = runBlocking {
        val existingCalendar = CalendarEntity("1", "Work", 0xFF0000, true)
        coEvery { mockCalendarDao.getCalendarById("1") } returns existingCalendar
        coEvery { mockCalendarDao.updateCalendar(any()) } just Runs

        repository.updateCalendarMonitoring("1", false)

        coVerify { mockCalendarDao.updateCalendar(existingCalendar.copy(isMonitored = false)) }
    }

    @Test
    fun `getMonitoredCalendars returns flow from dao`() = runBlocking {
        val calendarsFlow = flowOf(
            listOf(
                CalendarEntity("1", "Work", 0xFF0000, true)
            )
        )
        every { mockCalendarDao.getAllCalendars() } returns calendarsFlow

        val result = repository.getMonitoredCalendars().first()

        assertEquals(1, result.size)
        assertEquals("Work", result.first().name)
    }

    @Test
    fun `fetchAndStoreEventsForCalendar fetches and stores events`() = runBlocking {
        val calendarId = "1"
        val startMillis = 1672531200000L // Jan 1, 2023
        val endMillis = 1704067200000L // Jan 1, 2024

        val mockCursor = mockk<android.database.Cursor>()
        every { mockCursor.moveToNext() } returnsMany listOf(true, false)
        every { mockCursor.getLong(any()) } returns 1L // Event ID
        every { mockCursor.getString(any()) } returns "Test Event" // Title
        every { mockCursor.getLong(any()) } returns startMillis // Begin
        every { mockCursor.getLong(any()) } returns endMillis // End
        every { mockCursor.getInt(any()) } returns 0 // All Day
        every { mockCursor.getString(any()) } returns "Location" // Event Location
        every { mockCursor.getColumnIndexOrThrow(CalendarContract.Instances._ID) } returns 0
        every { mockCursor.getColumnIndexOrThrow(CalendarContract.Instances.TITLE) } returns 1
        every { mockCursor.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN) } returns 2
        every { mockCursor.getColumnIndexOrThrow(CalendarContract.Instances.END) } returns 3
        every { mockCursor.getColumnIndexOrThrow(CalendarContract.Instances.ALL_DAY) } returns 4
        every { mockCursor.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_LOCATION) } returns 5
        every { mockCursor.close() } just Runs

        every {
            mockContentResolver.query(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns mockCursor

        coEvery { mockEventDao.insertEvent(any()) } just Runs

        repository.fetchAndStoreEventsForCalendar(calendarId, startMillis, endMillis)

        coVerify { mockEventDao.insertEvent(any()) }
    }

    @Test
    fun `updateEventSeenStatus updates event in database`() = runBlocking {
        val eventId = 1L
        val isSeen = true
        coEvery { mockEventDao.updateEventSeenStatus(eventId, isSeen) } just Runs

        repository.updateEventSeenStatus(eventId, isSeen)

        coVerify { mockEventDao.updateEventSeenStatus(eventId, isSeen) }
    }
}