package net.npike.android.calendarnotify.ui.screen

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.npike.android.calendarnotify.data.local.SetupStatusManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class InitialSetupScreenViewModelTest {

    @MockK
    lateinit var mockSetupStatusManager: SetupStatusManager

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: InitialSetupScreenViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // Default mock behavior for setupStatusManager
        coEvery { mockSetupStatusManager.isInitialSetupComplete } returns flowOf(false)
        coEvery { mockSetupStatusManager.setInitialSetupComplete(any()) } returns Unit

        viewModel = InitialSetupScreenViewModel(mockSetupStatusManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading when setup is not complete`() = runTest {
        assertEquals(InitialSetupScreenViewModel.SetupUiState.Loading, viewModel.uiState.first())
    }

    @Test
    fun `initial state is complete when setup is complete`() = runTest {
        coEvery { mockSetupStatusManager.isInitialSetupComplete } returns flowOf(true)
        viewModel = InitialSetupScreenViewModel(mockSetupStatusManager)
        assertEquals(InitialSetupScreenViewModel.SetupUiState.Complete, viewModel.uiState.first())
    }

    @Test
    fun `startSetup sets state to loading and then complete`() = runTest {
        val setupCompleteFlow = MutableStateFlow(false)
        coEvery { mockSetupStatusManager.isInitialSetupComplete } returns setupCompleteFlow

        viewModel.startSetup()
        advanceUntilIdle()

        assertEquals(InitialSetupScreenViewModel.SetupUiState.Loading, viewModel.uiState.first())

        setupCompleteFlow.value = true
        advanceUntilIdle()

        assertEquals(InitialSetupScreenViewModel.SetupUiState.Complete, viewModel.uiState.first())
        assertTrue(setupCompleteFlow.first())
    }

    @Test
    fun `startSetup sets initial setup complete to true`() = runTest {
        viewModel.startSetup()
        advanceUntilIdle()
        coEvery { mockSetupStatusManager.setInitialSetupComplete(true) }
    }
}