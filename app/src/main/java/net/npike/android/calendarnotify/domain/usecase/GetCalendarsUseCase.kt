package net.npike.android.calendarnotify.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.npike.android.calendarnotify.data.repository.CalendarRepository
import net.npike.android.calendarnotify.domain.model.Calendar
import javax.inject.Inject

class GetCalendarsUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
) {

    suspend operator fun invoke(): Flow<List<Calendar>> {
        calendarRepository.fetchAndStoreSystemCalendars()
        calendarRepository.startObservingCalendarChanges()
        return calendarRepository.getMonitoredCalendars().map { calendarEntities ->
            calendarEntities.map { calendarEntity ->
                Calendar(
                    id = calendarEntity.id,
                    name = calendarEntity.name,
                    color = calendarEntity.color,
                    isMonitored = calendarEntity.isMonitored
                )
            }
        }
    }
}