package net.npike.android.calendarnotify.domain.usecase

import kotlinx.coroutines.flow.firstOrNull
import net.npike.android.calendarnotify.data.local.EventDao
import net.npike.android.calendarnotify.data.local.DataStoreManager
import net.npike.android.calendarnotify.data.repository.CalendarRepository
import net.npike.android.calendarnotify.domain.model.Event
import net.npike.android.calendarnotify.service.NotificationHelper
import timber.log.Timber
import javax.inject.Inject

class CheckForNewEventsUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val eventDao: EventDao,
    private val dataStoreManager: DataStoreManager,
    private val notificationHelper: NotificationHelper
) {
    suspend operator fun invoke() {
        val firstRunTimestamp = dataStoreManager.getFirstRunTimestamp()
        if (firstRunTimestamp == 0L) {
            // Should not happen if WorkManagerInitializer is called correctly, but as a safeguard
            Timber.w("First run timestamp is 0, aborting check for new events.")
            return
        }

        val monitoredCalendars = calendarRepository.getMonitoredCalendars()
            .firstOrNull() // Get the current list of calendars
            ?.filter { it.isMonitored } ?: emptyList()
        Timber.d("Found ${monitoredCalendars.size} monitored calendars.")

        val now = System.currentTimeMillis()
        val oneYearMillis = 365 * 24 * 60 * 60 * 1000L // Check for events in the next year

        monitoredCalendars.forEach { calendar ->
            Timber.d("Checking for events in calendar: ${calendar.name}")
            val events = calendarRepository.getEventsForCalendar(calendar.id, calendar.name, now, now + oneYearMillis, firstRunTimestamp)
            Timber.d("Found ${events.size} events in calendar ${calendar.name}")
            events.forEach { event ->
                Timber.d("Processing event: ${event.title} with start time ${event.startTime}")
                val existingEvent = eventDao.getEventById(event.id)

                if (existingEvent == null) {
                    Timber.d("Event is new. Event last date: ${event.lastDate}, first run timestamp: $firstRunTimestamp")
                    if (event.lastDate > firstRunTimestamp) { // Only consider new events after first run
                        Timber.d("Event is after first run. Inserting and notifying.")
                        // New event, insert and notify
                        eventDao.insertEvent(
                            net.npike.android.calendarnotify.data.local.EventEntity(
                                id = event.id,
                                calendarId = event.calendarId,
                                title = event.title,
                                startTime = event.startTime,
                                endTime = event.endTime,
                                isSeen = false // Mark as not seen initially
                            )
                        )
                        notificationHelper.showNewEventNotification(event)
                    } else {
                        Timber.d("Event is before first run. Ignoring.")
                    }
                } else {
                    Timber.d("Event already exists. Ignoring.")
                }
            }
        }
    }
}