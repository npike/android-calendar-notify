package net.npike.android.calendarnotify.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.npike.android.calendarnotify.domain.model.Calendar
import net.npike.android.calendarnotify.domain.usecase.GetCalendarsUseCase
import net.npike.android.calendarnotify.domain.usecase.UpdateCalendarUseCase
import javax.inject.Inject

@HiltViewModel
class CalendarScreenViewModel @Inject constructor(
    private val getCalendarsUseCase: GetCalendarsUseCase,
    private val updateCalendarUseCase: UpdateCalendarUseCase
) : ViewModel() {

    private val _calendars = MutableStateFlow<List<Calendar>>(emptyList())
    val calendars: StateFlow<List<Calendar>> = _calendars.asStateFlow()

    fun loadCalendars() {
        viewModelScope.launch {
            getCalendarsUseCase().collect {
                _calendars.value = it
            }
        }
    }

    fun onCalendarToggled(calendar: Calendar, isMonitored: Boolean) {
        viewModelScope.launch {
            updateCalendarUseCase(calendar.id, isMonitored)
        }
    }
}