package net.npike.android.calendarnotify.ui.screen

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.*
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest
import net.npike.android.calendarnotify.domain.model.Calendar
import net.npike.android.calendarnotify.domain.usecase.GetCalendarsUseCase
import net.npike.android.calendarnotify.domain.usecase.UpdateCalendarUseCase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
class CalendarScreenViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val mockGetCalendarsUseCase = mockk<GetCalendarsUseCase>()
    private val mockUpdateCalendarUseCase = mockk<UpdateCalendarUseCase>()
    private lateinit var viewModel: CalendarScreenViewModel

    private val testCalendars = listOf(
        Calendar("1", "Work", 0xFF0000, true),
        Calendar("2", "Personal", 0x00FF00, false)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { mockGetCalendarsUseCase() } returns MutableStateFlow(testCalendars)
        coEvery { mockUpdateCalendarUseCase(any(), any()) } just Runs
        viewModel = CalendarScreenViewModel(mockGetCalendarsUseCase, mockUpdateCalendarUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `init fetches calendars and updates state`() = testScope.runTest {
        val collectedCalendars = mutableListOf<List<Calendar>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.calendars.collect { collectedCalendars.add(it) }
        }

        advanceUntilIdle() // Advance time for the collect to emit

        assertEquals(1, collectedCalendars.size)
        assertEquals(testCalendars, collectedCalendars.first())
    }

    @Test
    fun `onCalendarToggled calls updateCalendarUseCase`() = testScope.runTest {
        val calendar = Calendar("1", "Work", 0xFF0000, true)
        val isMonitored = false

        viewModel.onCalendarToggled(calendar, isMonitored)
        advanceUntilIdle() // Advance time for the coroutine to complete

        coVerify { mockUpdateCalendarUseCase(calendar.id, isMonitored) }
    }
}