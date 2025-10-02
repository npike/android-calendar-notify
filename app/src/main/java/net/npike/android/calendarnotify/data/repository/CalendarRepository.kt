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
import net.npike.android.calendarnotify.data.local.EventDao
import net.npike.android.calendarnotify.data.local.EventEntity
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
    private val eventDao: EventDao,
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
                CalendarContract.Calendars.IS_PRIMARY
            )

            val cursor = contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                null,
                null,
                null
            )

            cursor?.use {
                while (it.moveToNext()) {
                    val id = it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars._ID))
                    val name = it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))
                    val color = it.getInt(it.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_COLOR))
                    val isPrimary = it.getInt(it.getColumnIndexOrThrow(CalendarContract.Calendars.IS_PRIMARY)) == 1

                    calendars.add(
                        Calendar(
                            id = id,
                            name = name,
                            color = color,
                            isMonitored = true // Default to monitored, will be overridden by DataStore
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

    suspend fun getEventsFromCalendarProvider(calendarId: String, startTime: Long, endTime: Long): List<EventEntity> {
        if (!hasReadCalendarPermission()) return emptyList()
        return withContext(Dispatchers.IO) {
            val events = mutableListOf<EventEntity>()
            val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
            ContentUris.appendId(builder, startTime)
            ContentUris.appendId(builder, endTime)
            val uri = builder.build()

            val projection = arrayOf(
                CalendarContract.Instances.EVENT_ID,
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.END,
                CalendarContract.Instances.CALENDAR_ID,
                CalendarContract.Instances.EVENT_LOCATION,
                CalendarContract.Instances.ALL_DAY
            )

            val selection = "${CalendarContract.Instances.CALENDAR_ID} = ?"
            val selectionArgs = arrayOf(calendarId)

            val cursor = contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                null
            )

            cursor?.use {
                while (it.moveToNext()) {
                    val eventId = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_ID)).toString()
                    val title = it.getString(it.getColumnIndexOrThrow(CalendarContract.Instances.TITLE))
                    val begin = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN))
                    val end = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Instances.END))
                    val eventLocation = it.getString(it.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_LOCATION))
                    val allDay = it.getInt(it.getColumnIndexOrThrow(CalendarContract.Instances.ALL_DAY)) == 1

                    events.add(
                        EventEntity(
                            id = eventId,
                            calendarId = calendarId,
                            title = title,
                            startTime = begin,
                            endTime = end,
                            isSeen = false, // Default to false when fetching
                            location = eventLocation,
                            isAllDay = allDay,
                            lastDate = 0L // Placeholder, as lastDate is not directly from Instances
                        )
                    )
                }
            }
            events
        }
    }

    suspend fun updateEventSeenStatus(eventId: String, isSeen: Boolean) {
        withContext(Dispatchers.IO) {
            try {
                eventDao.updateEventSeenStatus(eventId, isSeen)
            } catch (e: Exception) {
                Timber.e(e, "Error updating event seen status in database.")
            }
        }
    }
}