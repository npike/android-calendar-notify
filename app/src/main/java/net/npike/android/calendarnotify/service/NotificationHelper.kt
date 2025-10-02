package net.npike.android.calendarnotify.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.CalendarContract
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import net.npike.android.calendarnotify.R
import javax.inject.Inject
import javax.inject.Singleton

import net.npike.android.calendarnotify.domain.model.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.ContentUris

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val CHANNEL_ID = "new_event_notification_channel"
    private val CHANNEL_NAME = "New Event Notifications"
    private val NOTIFICATION_ID = 1001

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for new calendar events"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNewEventNotification(event: Event) {
        val title = "New shared calendar event on ${event.calendarName}"

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val startTime = timeFormat.format(Date(event.startTime))
        val endTime = timeFormat.format(Date(event.endTime))

        val timeText = if (event.isAllDay) {
            "All day"
        } else {
            "$startTime - $endTime"
        }

        var content = "${event.title}\n$timeText"
        if (!event.location.isNullOrEmpty()) {
            content += "\n${event.location}"
        }

        val eventUri: Uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.id.toLong())
        val viewIntent = Intent(Intent.ACTION_VIEW).setData(eventUri)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            event.id.hashCode(), // Use event ID hash code for unique request code
            viewIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // TODO: Replace with actual app icon
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // Set the PendingIntent
            .setAutoCancel(true) // Automatically removes the notification when the user taps it
            .setColor(event.calendarColor)

        with(NotificationManagerCompat.from(context)) {
            notify(event.id.hashCode(), builder.build())
        }
    }

    fun createForegroundServiceNotification(): Notification {
        createNotificationChannel() // Ensure channel exists
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Initial Setup")
            .setContentText("Fetching calendar events...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true) // Makes the notification non-dismissible
            .build()
    }
}