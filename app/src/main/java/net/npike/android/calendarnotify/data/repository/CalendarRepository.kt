package net.npike.android.calendarnotify.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.database.ContentObserver
import android.net.Uri
import android.provider.CalendarContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import net.npike.android.calendarnotify.data.local.CalendarDao
import net.npike.android.calendarnotify.data.local.CalendarEntity
import net.npike.android.calendarnotify.domain.model.Event
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
    private val calendarDao: CalendarDao
) {

    private fun hasReadCalendarPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val _systemCalendars = MutableStateFlow<List<CalendarEntity>>(emptyList())
    val systemCalendars: Flow<List<CalendarEntity>> = _systemCalendars.asStateFlow()

    private val calendarObserver = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            // Trigger a refresh of system calendars
            // This will be handled by WorkManager in a separate task
        }
    }

    fun startObservingCalendarChanges() {
        if (!hasReadCalendarPermission()) return
        contentResolver.registerContentObserver(
            CalendarContract.Calendars.CONTENT_URI,
            true,
            calendarObserver
        )
    }

    suspend fun fetchAndStoreSystemCalendars() {
        if (!hasReadCalendarPermission()) return
        withContext(Dispatchers.IO) {
            val projection = arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR
            )

            val cursor = contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                null,
                null,
                null
            )

            cursor?.use {
                val calendars = mutableListOf<CalendarEntity>()
                while (it.moveToNext()) {
                    val id = it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars._ID))
                    val name = it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))
                    val color = it.getInt(it.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_COLOR))

                    // Check if calendar already exists in local DB to preserve isMonitored status
                    val existingCalendar = calendarDao.getCalendarById(id)
                    calendars.add(
                        CalendarEntity(
                            id = id,
                            name = name,
                            color = color,
                            isMonitored = existingCalendar?.isMonitored ?: true // Default to monitored if new
                        )
                    )
                }
                calendarDao.insertAll(calendars) // Assuming insertAll is added to CalendarDao
                _systemCalendars.value = calendars
            }
        }
    }

    suspend fun updateCalendarMonitoring(calendarId: String, isMonitored: Boolean) {
        withContext(Dispatchers.IO) {
            calendarDao.getCalendarById(calendarId)?.let {
                calendarDao.updateCalendar(it.copy(isMonitored = isMonitored))
            }
        }
    }

    fun getMonitoredCalendars(): Flow<List<CalendarEntity>> {
        return calendarDao.getAllCalendars()
    }

    suspend fun getEventsForCalendar(calendarId: String, startTime: Long, endTime: Long, minLastDate: Long): List<Event> {
        if (!hasReadCalendarPermission()) return emptyList()
        val events = mutableListOf<Event>()
        val uriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(uriBuilder, startTime)
        ContentUris.appendId(uriBuilder, endTime)
        val uri = uriBuilder.build()

        val projection = arrayOf(
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.CALENDAR_ID,
            CalendarContract.Instances.LAST_DATE // Add LAST_DATE to projection
        )

        val selection = "${CalendarContract.Instances.CALENDAR_ID} = ? AND ${CalendarContract.Instances.LAST_DATE} > ?"
        val selectionArgs = arrayOf(calendarId, minLastDate.toString())

        val cursor = contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val eventId = it.getString(it.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_ID))
                val title = it.getString(it.getColumnIndexOrThrow(CalendarContract.Instances.TITLE))
                val begin = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN))
                val end = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Instances.END))
                val calId = it.getString(it.getColumnIndexOrThrow(CalendarContract.Instances.CALENDAR_ID))
                val lastDate = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Instances.LAST_DATE))

                events.add(Event(eventId, calId, title, begin, end, false)) // isSeen will be managed by local DB
            }
        }
        return events
    }
}