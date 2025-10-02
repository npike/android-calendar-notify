package net.npike.android.calendarnotify.service

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.npike.android.calendarnotify.domain.usecase.PrepopulateEventsUseCase
import timber.log.Timber
import java.util.Calendar

@HiltWorker
class FirstRunWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val prepopulateEventsUseCase: PrepopulateEventsUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("FirstRunWorker started.")

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.READ_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Timber.e("READ_CALENDAR permission not granted. Cannot perform first run event prepopulation.")
            return Result.failure()
        }

        return try {
            val now = Calendar.getInstance()
            val oneYearFromNow = Calendar.getInstance().apply { add(Calendar.YEAR, 1) }

            prepopulateEventsUseCase(now.timeInMillis, oneYearFromNow.timeInMillis)

            Timber.d("FirstRunWorker finished successfully.")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "FirstRunWorker failed.")
            Result.failure()
        }
    }
}
