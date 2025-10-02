package net.npike.android.calendarnotify

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import net.npike.android.calendarnotify.ui.screen.CalendarScreen
import net.npike.android.calendarnotify.ui.screen.CalendarScreenViewModel
import net.npike.android.calendarnotify.ui.screen.PermissionRationaleScreen
import net.npike.android.calendarnotify.ui.theme.CalendarNotifyTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CalendarNotifyTheme {
                val context = LocalContext.current
                var permissionGranted by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) }

                val launcher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    permissionGranted = isGranted
                }

                if (permissionGranted) {
                    val viewModel: CalendarScreenViewModel = hiltViewModel()
                    LaunchedEffect(Unit) {
                        viewModel.loadCalendars()
                    }
                    val calendars by viewModel.calendars.collectAsState()
                    CalendarScreen(calendars = calendars) { calendar, isMonitored ->
                        viewModel.onCalendarToggled(calendar, isMonitored)
                    }
                } else {
                    PermissionRationaleScreen {
                        launcher.launch(Manifest.permission.READ_CALENDAR)
                    }
                }
            }
        }
    }
}