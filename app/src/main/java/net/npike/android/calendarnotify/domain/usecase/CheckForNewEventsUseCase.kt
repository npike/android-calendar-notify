package net.npike.android.calendarnotify.domain.usecase

import kotlinx.coroutines.flow.first
import net.npike.android.calendarnotify.data.local.DataStoreManager
import net.npike.android.calendarnotify.data.repository.CalendarRepository
import net.npike.android.calendarnotify.domain.model.Event
import net.npike.android.calendarnotify.service.NotificationHelper
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

class CheckForNewEventsUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val dataStoreManager: DataStoreManager,
    private val notificationHelper: NotificationHelper
) {
    suspend operator fun invoke() {
        val monitoredCalendars = calendarRepository.getSystemCalendars()
            .first() // Get the current list of calendars
            .filter { it.isMonitored }
        Timber.d("Found ${monitoredCalendars.size} monitored calendars.")

        val lastKnownEventId = dataStoreManager.lastKnownEventId.first()
        var highestEventId = lastKnownEventId

        monitoredCalendars.forEach { calendar ->
            Timber.d("Checking for events in calendar: ${calendar.name} since event ID $lastKnownEventId")

            val eventsFromProvider = calendarRepository.getEventsFromCalendarProviderSinceEventId(
                calendar.id,
                lastKnownEventId
            )

            eventsFromProvider.forEach { event ->
                // This is a new event, show notification
                notificationHelper.showNewEventNotification(event.copy(calendarName = calendar.name, calendarColor = calendar.color))

                // Update highest event ID
                event.id.toLongOrNull()?.let {
                    if (it > highestEventId) {
                        highestEventId = it
                    }
                }
            }
        }

        if (highestEventId > lastKnownEventId) {
            dataStoreManager.setLastKnownEventId(highestEventId)
            Timber.d("Updated last known event ID to $highestEventId")
        }
    }
}