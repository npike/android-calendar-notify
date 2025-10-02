package net.npike.android.calendarnotify.domain.usecase

import kotlinx.coroutines.flow.first
import net.npike.android.calendarnotify.data.local.EventDao
import net.npike.android.calendarnotify.data.local.DataStoreManager
import net.npike.android.calendarnotify.data.repository.CalendarRepository
import net.npike.android.calendarnotify.domain.model.Event
import net.npike.android.calendarnotify.service.NotificationHelper
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

class CheckForNewEventsUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val eventDao: EventDao,
    private val dataStoreManager: DataStoreManager,
    private val notificationHelper: NotificationHelper
) {
    suspend operator fun invoke() {
        val monitoredCalendars = calendarRepository.getSystemCalendars()
            .first() // Get the current list of calendars
            .filter { it.isMonitored }
        Timber.d("Found ${monitoredCalendars.size} monitored calendars.")

        val now = Calendar.getInstance()
        val oneYearFromNow = Calendar.getInstance().apply { add(Calendar.YEAR, 1) }

        monitoredCalendars.forEach { calendar ->
            Timber.d("Checking for events in calendar: ${calendar.name}")

            val eventsFromProvider = calendarRepository.getEventsFromCalendarProvider(
                calendar.id,
                now.timeInMillis,
                oneYearFromNow.timeInMillis
            )

            val localEvents = eventDao.getAllEvents().first() ?: emptyList()
            val localEventIds = localEvents.map { it.id }.toSet()

            eventsFromProvider.forEach { eventEntity ->
                if (eventEntity.id !in localEventIds) {
                    // This is a new event, insert it and show notification
                    eventDao.insertEvent(eventEntity)
                    // Convert EventEntity to domain.model.Event for notification
                    val domainEvent = Event(
                        id = eventEntity.id,
                        calendarId = eventEntity.calendarId,
                        calendarName = calendar.name, // Use calendar name from monitoredCalendars
                        title = eventEntity.title,
                        startTime = eventEntity.startTime,
                        endTime = eventEntity.endTime,
                        isSeen = eventEntity.isSeen,
                        location = eventEntity.location,
                        isAllDay = eventEntity.isAllDay,
                        lastDate = eventEntity.lastDate
                    )
                    notificationHelper.showNewEventNotification(domainEvent)
                }
                // Mark event as seen after processing (or after notification is shown)
                calendarRepository.updateEventSeenStatus(eventEntity.id, true)
            }
        }
    }
}