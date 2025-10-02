package net.npike.android.calendarnotify.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var eventDao: EventDao
    private lateinit var calendarDao: CalendarDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        eventDao = db.eventDao()
        calendarDao = db.calendarDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeCalendarAndReadInList() = runBlocking {
        val calendar = CalendarEntity("1", "Test Calendar", 0xFF0000, true)
        calendarDao.insertCalendar(calendar)
        val byId = calendarDao.getCalendarById("1")
        assertEquals(byId?.name, calendar.name)

        val calendars = calendarDao.getAllCalendars().first()
        assertEquals(1, calendars.size)
        assertEquals(calendar.name, calendars[0].name)
    }

    @Test
    @Throws(Exception::class)
    fun writeEventAndReadInList() = runBlocking {
        val event = EventEntity("e1", "1", "Test Event", 100L, 200L, false)
        eventDao.insertEvent(event)
        val byId = eventDao.getEventById("e1")
        assertEquals(byId?.title, event.title)

        val events = eventDao.getAllEvents().first()
        assertEquals(1, events.size)
        assertEquals(event.title, events[0].title)
    }

    @Test
    @Throws(Exception::class)
    fun updateEventSeenStatus() = runBlocking {
        val event = EventEntity("e1", "1", "Test Event", 100L, 200L, false)
        eventDao.insertEvent(event)

        eventDao.updateEventSeenStatus("e1", true)
        val updatedEvent = eventDao.getEventById("e1")
        assertEquals(true, updatedEvent?.isSeen)
    }
}