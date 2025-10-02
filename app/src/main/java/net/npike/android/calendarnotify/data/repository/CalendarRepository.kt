package net.npike.android.calendarnotify.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.CalendarContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import net.npike.android.calendarnotify.data.local.DataStoreManager
import net.npike.android.calendarnotify.domain.model.Event
import net.npike.android.calendarnotify.domain.model.Calendar
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class CalendarRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contentResolver: ContentResolver,
    private val dataStoreManager: DataStoreManager
) {

    private fun hasReadCalendarPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getSystemCalendars(): Flow<List<Calendar>> = combine(
        flow { emit(querySystemCalendars()) },
        dataStoreManager.unmonitoredCalendarIds
    ) { systemCalendars: List<Calendar>, unmonitoredIds: Set<String> ->
        systemCalendars.map { calendar ->
            calendar.copy(isMonitored = calendar.id !in unmonitoredIds)
        }
    }

    private suspend fun querySystemCalendars(): List<Calendar> {
        if (!hasReadCalendarPermission()) return emptyList()
        return withContext(Dispatchers.IO) {
            val calendars = mutableListOf<Calendar>()
            val projection = arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR,
                CalendarContract.Calendars.IS_PRIMARY,
                CalendarContract.Calendars.SYNC_EVENTS
            )

            val sortOrder = "${CalendarContract.Calendars.CALENDAR_DISPLAY_NAME} COLLATE NOCASE ASC"
            val cursor = contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            cursor?.use {
                while (it.moveToNext()) {
                    val id = it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars._ID))
                    val name = it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))
                    val color = it.getInt(it.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_COLOR))
                    val isPrimary = it.getInt(it.getColumnIndexOrThrow(CalendarContract.Calendars.IS_PRIMARY)) == 1
                    val isSynced = it.getInt(it.getColumnIndexOrThrow(CalendarContract.Calendars.SYNC_EVENTS)) == 1

                    calendars.add(
                        Calendar(
                            id = id,
                            name = name,
                            color = color,
                            isMonitored = isSynced, // Default to monitored if synced
                            isSynced = isSynced
                        )
                    )
                }
            }
            calendars
        }
    }

    suspend fun updateCalendarMonitoring(calendarId: String, isMonitored: Boolean) {
        dataStoreManager.unmonitoredCalendarIds.first().let { currentUnmonitoredIds: Set<String> ->
            val newUnmonitoredIds = if (isMonitored) {
                currentUnmonitoredIds - calendarId
            } else {
                currentUnmonitoredIds + calendarId
            }
            dataStoreManager.setUnmonitoredCalendarIds(newUnmonitoredIds)
        }
    }

    suspend fun getEventsFromCalendarProviderSinceEventId(calendarId: String, lastEventId: Long): List<Event> {
        if (!hasReadCalendarPermission()) return emptyList()
        return withContext(Dispatchers.IO) {
            val events = mutableListOf<Event>()
            val uri = CalendarContract.Events.CONTENT_URI

            val projection = arrayOf(
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.CALENDAR_ID,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.ALL_DAY,
                CalendarContract.Events.LAST_DATE
            )

            val selection = "${CalendarContract.Events.CALENDAR_ID} = ? AND ${CalendarContract.Events._ID} > ?"
            val selectionArgs = arrayOf(calendarId, lastEventId.toString())

            val cursor = contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                null
            )

            cursor?.use {
                while (it.moveToNext()) {
                    val eventId = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Events._ID))
                    val title = it.getString(it.getColumnIndexOrThrow(CalendarContract.Events.TITLE))
                    val begin = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Events.DTSTART))
                    val end = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Events.DTEND))
                    val eventLocation = it.getString(it.getColumnIndexOrThrow(CalendarContract.Events.EVENT_LOCATION))
                    val allDay = it.getInt(it.getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY)) == 1
                    val lastDate = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Events.LAST_DATE))

                    events.add(
                        Event(
                            id = eventId.toString(),
                            calendarId = calendarId,
                            calendarName = "", // This will be filled in by the use case
                            title = title,
                            startTime = begin,
                            endTime = end,
                            isSeen = false, // Default to false when fetching
                            location = eventLocation,
                            isAllDay = allDay,
                            lastDate = lastDate
                        )
                    )
                }
            }
            events
        }
    }

    suspend fun getHighestEventId(): Long {
        if (!hasReadCalendarPermission()) return 0L
        return withContext(Dispatchers.IO) {
            var highestEventId = 0L
            val uri = CalendarContract.Events.CONTENT_URI
            val projection = arrayOf(CalendarContract.Events._ID)
            val sortOrder = "${CalendarContract.Events._ID} DESC"

            val cursor = contentResolver.query(
                uri,
                projection,
                null,
                null,
                sortOrder
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    highestEventId = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Events._ID))
                }
            }
            highestEventId
        }
    }
}