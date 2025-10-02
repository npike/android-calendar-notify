package net.npike.android.calendarnotify.ui.screen

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.work.WorkManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import net.npike.android.calendarnotify.data.local.SetupStatusManager
import net.npike.android.calendarnotify.data.repository.CalendarRepository
import net.npike.android.calendarnotify.domain.model.Calendar
import net.npike.android.calendarnotify.domain.usecase.GetCalendarsUseCase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CalendarScreenViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockGetCalendarsUseCase: GetCalendarsUseCase = mockk(relaxed = true)
    private val mockCalendarRepository: CalendarRepository = mockk(relaxed = true)
    private val mockSetupStatusManager: SetupStatusManager = mockk(relaxed = true)
    private val mockWorkManager: WorkManager = mockk(relaxed = true)

    private lateinit var viewModel: CalendarScreenViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = CalendarScreenViewModel(
            getCalendarsUseCase = mockGetCalendarsUseCase,
            calendarRepository = mockCalendarRepository,
            setupStatusManager = mockSetupStatusManager,
            workManager = mockWorkManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCalendars updates calendars state`() = runBlocking {
        // Arrange
        val calendars = listOf(Calendar("1", "Work", 0, true))
        coEvery { mockGetCalendarsUseCase() } returns flowOf(calendars)

        // Act
        viewModel.loadCalendars()

        // Assert
        assertEquals(calendars, viewModel.calendars.value)
    }

    @Test
    fun `onCalendarToggled calls repository`() = runBlocking {
        // Arrange
        val calendar = Calendar("1", "Work", 0, true)

        // Act
        viewModel.onCalendarToggled(calendar, false)

        // Assert
        coVerify { mockCalendarRepository.updateCalendarMonitoring("1", false) }
    }
}
