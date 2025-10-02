package net.npike.android.calendarnotify

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
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
import net.npike.android.calendarnotify.ui.screen.InitialSetupScreen
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
                val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                } else {
                    arrayOf(Manifest.permission.READ_CALENDAR)
                }

                var permissionsGranted by remember {
                    mutableStateOf(
                        permissions.all {
                            ContextCompat.checkSelfPermission(
                                context,
                                it
                            ) == PackageManager.PERMISSION_GRANTED
                        }
                    )
                }

                val launcher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissionsMap ->
                    permissionsGranted = permissionsMap.values.all { it }
                }

                if (permissionsGranted) {
                    val calendarScreenViewModel: CalendarScreenViewModel = hiltViewModel()
                    val setupUiState by calendarScreenViewModel.setupUiState.collectAsState()

                    LaunchedEffect(permissionsGranted) {
                        if (permissionsGranted) {
                            calendarScreenViewModel.startInitialSetup()
                        }
                    }

                    when (setupUiState) {
                        CalendarScreenViewModel.SetupUiState.Complete -> {
                            LaunchedEffect(Unit) {
                                calendarScreenViewModel.loadCalendars()
                            }
                            val calendars by calendarScreenViewModel.calendars.collectAsState()
                            CalendarScreen(calendars = calendars) { calendar, isMonitored ->
                                calendarScreenViewModel.onCalendarToggled(calendar, isMonitored)
                            }
                        }
                        else -> {
                            InitialSetupScreen(
                                uiState = setupUiState,
                                onSetupComplete = { /* Handled by ViewModel state */ }
                            )
                        }
                    }
                } else {
                    PermissionRationaleScreen {
                        launcher.launch(permissions)
                    }
                }
            }
        }
    }
}