package net.npike.android.calendarnotify.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.npike.android.calendarnotify.domain.usecase.CheckForNewEventsUseCase
import timber.log.Timber

@HiltWorker
class EventWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val checkForNewEventsUseCase: CheckForNewEventsUseCase,
    private val workManagerInitializer: WorkManagerInitializer // Inject WorkManagerInitializer
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("EventWorker started.")
        return try {
            checkForNewEventsUseCase()

            // Removed re-enqueueing logic from here. WorkManagerInitializer handles initial enqueueing.

            Timber.d("EventWorker finished successfully.")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "EventWorker failed.")
            Result.failure()
        }
    }
}