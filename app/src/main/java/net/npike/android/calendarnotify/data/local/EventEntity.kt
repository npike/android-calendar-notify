package net.npike.android.calendarnotify.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val calendarId: String,
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val isSeen: Boolean,
    val location: String?,
    val isAllDay: Boolean,
    val lastDate: Long
)