package net.npike.android.calendarnotify.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import net.npike.android.calendarnotify.data.local.DataStoreManager
import net.npike.android.calendarnotify.domain.model.Event
import net.npike.android.calendarnotify.data.repository.CalendarRepository
import net.npike.android.calendarnotify.domain.model.Calendar
import net.npike.android.calendarnotify.service.NotificationHelper
import org.junit.Before
import org.junit.Test

class CheckForNewEventsUseCaseTest {

    private val mockCalendarRepository: CalendarRepository = mockk(relaxed = true)
    private val mockDataStoreManager: DataStoreManager = mockk(relaxed = true)
    private val mockNotificationHelper: NotificationHelper = mockk(relaxed = true)
    private lateinit var useCase: CheckForNewEventsUseCase

    @Before
    fun setup() {
        useCase = CheckForNewEventsUseCase(
            calendarRepository = mockCalendarRepository,
            dataStoreManager = mockDataStoreManager,
            notificationHelper = mockNotificationHelper
        )
    }

    @Test
    fun `invoke shows notifications for new events and updates last known event ID`() = runBlocking {
        // Arrange
        val monitoredCalendars = listOf(Calendar("1", "Work", 0, true))
        coEvery { mockCalendarRepository.getSystemCalendars() } returns flowOf(monitoredCalendars)
        coEvery { mockDataStoreManager.lastKnownEventId } returns flowOf(100L)

        val newEvents = listOf(
            Event("101", "1", "Work", "New Meeting", 0L, 0L, false, null, false, 0L),
            Event("102", "1", "Work", "Another Meeting", 0L, 0L, false, null, false, 0L)
        )
        coEvery { mockCalendarRepository.getEventsFromCalendarProviderSinceEventId("1", 100L) } returns newEvents

        // Act
        useCase.invoke()

        // Assert
        coVerify(exactly = 2) { mockNotificationHelper.showNewEventNotification(any()) }
        coVerify(exactly = 1) { mockDataStoreManager.setLastKnownEventId(102L) }
    }

    @Test
    fun `invoke does nothing when there are no new events`() = runBlocking {
        // Arrange
        val monitoredCalendars = listOf(Calendar("1", "Work", 0, true))
        coEvery { mockCalendarRepository.getSystemCalendars() } returns flowOf(monitoredCalendars)
        coEvery { mockDataStoreManager.lastKnownEventId } returns flowOf(100L)
        coEvery { mockCalendarRepository.getEventsFromCalendarProviderSinceEventId("1", 100L) } returns emptyList()

        // Act
        useCase.invoke()

        // Assert
        coVerify(exactly = 0) { mockNotificationHelper.showNewEventNotification(any()) }
        coVerify(exactly = 0) { mockDataStoreManager.setLastKnownEventId(any()) }
    }
}
