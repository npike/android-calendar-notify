package net.npike.android.calendarnotify.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequestBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.npike.android.calendarnotify.data.local.SetupStatusManager
import net.npike.android.calendarnotify.data.repository.CalendarRepository
import net.npike.android.calendarnotify.domain.model.Calendar
import net.npike.android.calendarnotify.domain.usecase.GetCalendarsUseCase
import net.npike.android.calendarnotify.service.FirstRunWorker
import javax.inject.Inject

@HiltViewModel
class CalendarScreenViewModel @Inject constructor(
    private val getCalendarsUseCase: GetCalendarsUseCase,
    private val calendarRepository: CalendarRepository,
    private val setupStatusManager: SetupStatusManager,
    private val workManager: WorkManager
) : ViewModel() {

    private val _calendars = MutableStateFlow<List<Calendar>>(emptyList())
    val calendars: StateFlow<List<Calendar>> = _calendars.asStateFlow()

    private val _setupUiState = MutableStateFlow<SetupUiState>(SetupUiState.Loading)
    val setupUiState: StateFlow<SetupUiState> = _setupUiState.asStateFlow()

    sealed class SetupUiState {
        object Loading : SetupUiState()
        object Running : SetupUiState()
        object Complete : SetupUiState()
        data class Error(val message: String) : SetupUiState()
    }

    init {
        viewModelScope.launch {
            setupStatusManager.isInitialSetupComplete.collect {
                if (it) {
                    _setupUiState.value = SetupUiState.Complete
                } else {
                    _setupUiState.value = SetupUiState.Loading
                }
            }
        }
    }

    fun loadCalendars() {
        viewModelScope.launch {
            getCalendarsUseCase().collect {
                _calendars.value = it
            }
        }
    }

    fun onCalendarToggled(calendar: Calendar, isMonitored: Boolean) {
        if (!calendar.isSynced) return
        viewModelScope.launch {
            calendarRepository.updateCalendarMonitoring(calendar.id, isMonitored)
        }
    }

    fun startInitialSetup() {
        viewModelScope.launch {
            // Only run setup if it hasn't been completed before
            if (!setupStatusManager.isInitialSetupComplete.first()) {
                _setupUiState.value = SetupUiState.Running
                val firstRunWorkRequest = OneTimeWorkRequestBuilder<FirstRunWorker>().build()
                workManager.enqueue(firstRunWorkRequest)

                workManager.getWorkInfoByIdFlow(firstRunWorkRequest.id)
                    .filterNotNull()
                    .collect {
                        when (it.state) {
                            WorkInfo.State.SUCCEEDED -> {
                                setupStatusManager.setInitialSetupComplete(true)
                                _setupUiState.value = SetupUiState.Complete
                            }
                            WorkInfo.State.FAILED -> {
                                setupStatusManager.setInitialSetupComplete(false)
                                _setupUiState.value = SetupUiState.Error("Initial setup failed.")
                            }
                            else -> {
                                // Do nothing for other states like ENQUEUED, RUNNING, BLOCKED, CANCELLED
                            }
                        }
                    }
            } else {
                _setupUiState.value = SetupUiState.Complete
            }
        }
    }
}