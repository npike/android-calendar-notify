package net.npike.android.calendarnotify.domain.usecase

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import net.npike.android.calendarnotify.data.local.CalendarEntity
import net.npike.android.calendarnotify.data.repository.CalendarRepository
import net.npike.android.calendarnotify.domain.model.Calendar
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCalendarsUseCaseTest {

    private val mockCalendarRepository = mockk<CalendarRepository>()
    private lateinit var getCalendarsUseCase: GetCalendarsUseCase

    @Before
    fun setup() {
        getCalendarsUseCase = GetCalendarsUseCase(mockCalendarRepository)
    }

    @Test
    fun `invoke returns mapped calendars from repository`() = runBlocking {
        val calendarEntities = listOf(
            CalendarEntity("1", "Work", 0xFF0000, true),
            CalendarEntity("2", "Personal", 0x00FF00, false)
        )
        coEvery { mockCalendarRepository.fetchAndStoreSystemCalendars() } returns Unit
        every { mockCalendarRepository.startObservingCalendarChanges() } returns Unit
        every { mockCalendarRepository.getMonitoredCalendars() } returns flowOf(calendarEntities)

        val result = getCalendarsUseCase().first()

        assertEquals(2, result.size)
        assertEquals(Calendar("1", "Work", 0xFF0000, true), result[0])
        assertEquals(Calendar("2", "Personal", 0x00FF00, false), result[1])
    }
}