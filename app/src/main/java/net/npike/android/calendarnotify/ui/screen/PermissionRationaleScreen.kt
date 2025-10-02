package net.npike.android.calendarnotify.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.npike.android.calendarnotify.ui.theme.CalendarNotifyTheme

@Composable
fun PermissionRationaleScreen(onPermissionRequested: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Calendar access is required to monitor your events and send notifications. Please grant the permission to continue.",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = onPermissionRequested) {
            Text("Grant Permission")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionRationaleScreenPreview() {
    CalendarNotifyTheme {
        PermissionRationaleScreen(onPermissionRequested = {})
    }
}