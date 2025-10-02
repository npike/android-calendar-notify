package net.npike.android.calendarnotify.domain.usecase

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import net.npike.android.calendarnotify.data.repository.CalendarRepository
import net.npike.android.calendarnotify.domain.model.Calendar
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCalendarsUseCaseTest {

    private val mockCalendarRepository: CalendarRepository = mockk(relaxed = true)
    private lateinit var useCase: GetCalendarsUseCase

    @Before
    fun setup() {
        useCase = GetCalendarsUseCase(mockCalendarRepository)
    }

    @Test
    fun `invoke returns calendars from repository`() = runBlocking {
        // Arrange
        val calendars = listOf(
            Calendar("1", "Work", 0xFF0000, true, isSynced = true),
            Calendar("2", "Personal", 0x00FF00, false, isSynced = true)
        )
        coEvery { mockCalendarRepository.getSystemCalendars() } returns flowOf(calendars)

        // Act
        val result = useCase.invoke().first()

        // Assert
        assertEquals(2, result.size)
        assertEquals(calendars, result)
    }
}
