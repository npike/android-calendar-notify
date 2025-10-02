package net.npike.android.calendarnotify.service

import android.content.Context
import android.provider.CalendarContract
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import net.npike.android.calendarnotify.data.local.DataStoreManager
import kotlinx.coroutines.runBlocking // Import runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStoreManager: DataStoreManager
) {

    private val workManager = WorkManager.getInstance(context)

    fun initialize() {
        runBlocking { // Use runBlocking for suspend functions in initialize
            // Persist first run timestamp if not already set
            if (dataStoreManager.getFirstRunTimestamp() == 0L) {
                dataStoreManager.setFirstRunTimestamp(System.currentTimeMillis())
            }
        }

        // Enqueue the ContentUriTrigger worker
        enqueueContentUriWorker()

        // Removed PeriodicWorkRequest for now to isolate ContentUriTrigger issue
    }

    fun enqueueContentUriWorker() {
        val contentUriConstraints = Constraints.Builder()
            .addContentUriTrigger(CalendarContract.Events.CONTENT_URI, true)
            .build()

        val contentUriWorkRequest = OneTimeWorkRequestBuilder<EventWorker>()
            .setConstraints(contentUriConstraints)
            .build()

        // Use ExistingWorkPolicy.REPLACE to ensure a new worker is always enqueued
        workManager.enqueueUniqueWork(
            "EventContentUriWork",
            ExistingWorkPolicy.REPLACE,
            contentUriWorkRequest
        )
    }
}