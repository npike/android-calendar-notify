package net.npike.android.calendarnotify.domain.model

data class Event(
    val id: String,
    val calendarId: String,
    val calendarName: String,
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val isSeen: Boolean,
    val location: String?,
    val isAllDay: Boolean,
    val lastDate: Long,
    val calendarColor: Int = 0
)