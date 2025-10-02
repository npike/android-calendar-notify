package net.npike.android.calendarnotify.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.npike.android.calendarnotify.domain.model.Calendar
import net.npike.android.calendarnotify.ui.theme.CalendarNotifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    calendars: List<Calendar>,
    onCalendarToggled: (Calendar, Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Calendar Notifier") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            items(calendars) { calendar ->
                CalendarItem(calendar = calendar) { isMonitored ->
                    onCalendarToggled(calendar, isMonitored)
                }
            }
        }
    }
}

@Composable
fun CalendarItem(calendar: Calendar, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color(calendar.color))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = calendar.name, style = MaterialTheme.typography.bodyLarge)
                if (!calendar.isSynced) {
                    Text(
                        text = "This calendar is not currently being synced",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Switch(
            checked = calendar.isMonitored,
            onCheckedChange = onToggle,
            enabled = calendar.isSynced
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarItemPreview() {
    CalendarNotifyTheme {
        CalendarItem(calendar = Calendar("1", "Work Calendar", 0xFF0000, true, true)) {}
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CalendarItemDarkPreview() {
    CalendarNotifyTheme(darkTheme = true) {
        CalendarItem(calendar = Calendar("1", "Work Calendar", 0xFF0000, true, false)) {}
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    CalendarNotifyTheme {
        CalendarScreen(calendars = listOf(
            Calendar("1", "Work Calendar", 0xFF0000, true, true),
            Calendar("2", "Personal Calendar", 0x00FF00, false, true),
            Calendar("3", "Not Synced", 0x0000FF, false, false)
        )) { _, _ -> }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CalendarScreenDarkPreview() {
    CalendarNotifyTheme(darkTheme = true) {
        CalendarScreen(calendars = listOf(
            Calendar("1", "Work Calendar", 0xFF0000, true, true),
            Calendar("2", "Personal Calendar", 0x00FF00, false, true),
            Calendar("3", "Not Synced", 0x0000FF, false, false)
        )) { _, _ -> }
    }
}