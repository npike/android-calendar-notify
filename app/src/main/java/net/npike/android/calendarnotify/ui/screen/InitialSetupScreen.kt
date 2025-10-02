package net.npike.android.calendarnotify.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.npike.android.calendarnotify.R
import net.npike.android.calendarnotify.ui.theme.CalendarNotifyTheme

@Composable
fun InitialSetupScreen(
    uiState: CalendarScreenViewModel.SetupUiState,
    onSetupComplete: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (uiState) {
            CalendarScreenViewModel.SetupUiState.Loading -> {
                CircularProgressIndicator()
                Text(
                    text = stringResource(id = R.string.initial_setup_loading),
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            CalendarScreenViewModel.SetupUiState.Running -> {
                CircularProgressIndicator()
                Text(
                    text = stringResource(id = R.string.initial_setup_running),
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            is CalendarScreenViewModel.SetupUiState.Error -> {
                Text(
                    text = stringResource(id = R.string.initial_setup_error, uiState.message),
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
            CalendarScreenViewModel.SetupUiState.Complete -> {
                Text(
                    text = stringResource(id = R.string.initial_setup_complete),
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                onSetupComplete()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InitialSetupScreenPreview() {
    CalendarNotifyTheme {
        InitialSetupScreen(
            uiState = CalendarScreenViewModel.SetupUiState.Loading,
            onSetupComplete = {}
        )
    }
}