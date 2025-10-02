package net.npike.android.calendarnotify.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendars")
data class CalendarEntity(
    @PrimaryKey val id: String,
    val name: String,
    val color: Int,
    val isMonitored: Boolean
)