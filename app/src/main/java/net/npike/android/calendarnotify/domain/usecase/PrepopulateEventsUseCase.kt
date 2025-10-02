package net.npike.android.calendarnotify.domain.usecase

import kotlinx.coroutines.flow.first
import net.npike.android.calendarnotify.data.local.EventDao
import net.npike.android.calendarnotify.data.repository.CalendarRepository
import timber.log.Timber
import javax.inject.Inject

class PrepopulateEventsUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val eventDao: EventDao
) {
    suspend operator fun invoke(startTime: Long, endTime: Long) {
        val monitoredCalendars = calendarRepository.getSystemCalendars()
            .first() // Get the current list of calendars
            .filter { it.isMonitored }
        Timber.d("Found ${monitoredCalendars.size} monitored calendars for prepopulation.")

        monitoredCalendars.forEach { calendar ->
            Timber.d("Prepopulating events for calendar: ${calendar.name} from $startTime to $endTime")

            val eventsFromProvider = calendarRepository.getEventsFromCalendarProvider(
                calendar.id,
                startTime,
                endTime
            )

            eventsFromProvider.forEach { eventEntity ->
                // Insert event and mark as seen immediately, without notification
                eventDao.insertEvent(eventEntity.copy(isSeen = true))
            }
        }
    }
}
