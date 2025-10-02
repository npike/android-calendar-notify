package net.npike.android.calendarnotify.domain.usecase

import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import net.npike.android.calendarnotify.data.local.CalendarEntity
import net.npike.android.calendarnotify.data.local.DataStoreManager
import net.npike.android.calendarnotify.data.local.EventDao
import net.npike.android.calendarnotify.data.local.EventEntity
import net.npike.android.calendarnotify.data.repository.CalendarRepository
import net.npike.android.calendarnotify.domain.model.Event
import net.npike.android.calendarnotify.service.NotificationHelper
import org.junit.Before
import org.junit.Test

class CheckForNewEventsUseCaseTest {

    private val mockCalendarRepository = mockk<CalendarRepository>()
    private val mockEventDao = mockk<EventDao>()
    private val mockDataStoreManager = mockk<DataStoreManager>()
    private val mockNotificationHelper = mockk<NotificationHelper>(relaxed = true)
    private lateinit var checkForNewEventsUseCase: CheckForNewEventsUseCase

    @Before
    fun setup() {
        clearAllMocks()
        checkForNewEventsUseCase = CheckForNewEventsUseCase(mockCalendarRepository, mockEventDao, mockDataStoreManager, mockNotificationHelper)
    }

    @Test
    fun `invoke inserts new events and does not insert existing events`() = runBlocking {
        val firstRunTimestamp = 100L
        coEvery { mockDataStoreManager.getFirstRunTimestamp() } returns firstRunTimestamp

        val monitoredCalendarEntities = listOf(
            CalendarEntity("1", "Work", 0xFF0000, true)
        )
        every { mockCalendarRepository.getMonitoredCalendars() } returns flowOf(monitoredCalendarEntities)

        val newEvent = Event("e1", "1", "Work", "Meeting", 1000L, 2000L, false, null, false, 200L)
        val existingEvent = Event("e2", "1", "Work", "Lunch", 3000L, 4000L, true, null, false, 200L)

        coEvery { mockCalendarRepository.getEventsForCalendar(any(), any(), any(), any(), any()) } returns listOf(newEvent, existingEvent)
        coEvery { mockEventDao.getEventById(newEvent.id) } returns null
        coEvery { mockEventDao.getEventById(existingEvent.id) } returns EventEntity("e2", "1", "Lunch", 3000L, 4000L, true)
        coEvery { mockEventDao.insertEvent(any()) } just Runs

        checkForNewEventsUseCase()

        coVerify(exactly = 1) { mockEventDao.insertEvent(any()) }
        coVerify { mockEventDao.insertEvent(any()) }
    }
}