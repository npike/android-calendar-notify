package net.npike.android.calendarnotify.domain.usecase

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.npike.android.calendarnotify.data.repository.CalendarRepository
import org.junit.Before
import org.junit.Test

class UpdateCalendarUseCaseTest {

    private val mockCalendarRepository = mockk<CalendarRepository>()
    private lateinit var updateCalendarUseCase: UpdateCalendarUseCase

    @Before
    fun setup() {
        updateCalendarUseCase = UpdateCalendarUseCase(mockCalendarRepository)
    }

    @Test
    fun `invoke calls updateCalendarMonitoring on repository`() = runBlocking {
        val calendarId = "1"
        val isMonitored = false
        coEvery { mockCalendarRepository.updateCalendarMonitoring(calendarId, isMonitored) } just Runs

        updateCalendarUseCase(calendarId, isMonitored)

        coVerify { mockCalendarRepository.updateCalendarMonitoring(calendarId, isMonitored) }
    }
}