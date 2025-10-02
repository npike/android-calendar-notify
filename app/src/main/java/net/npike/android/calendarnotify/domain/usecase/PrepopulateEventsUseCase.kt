package net.npike.android.calendarnotify.domain.usecase

import net.npike.android.calendarnotify.data.local.DataStoreManager
import net.npike.android.calendarnotify.data.repository.CalendarRepository
import timber.log.Timber
import javax.inject.Inject

class PrepopulateEventsUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val dataStoreManager: DataStoreManager
) {
    suspend operator fun invoke() {
        Timber.d("Prepopulating last known event ID")
        val highestEventId = calendarRepository.getHighestEventId()
        dataStoreManager.setLastKnownEventId(highestEventId)
        Timber.d("Last known event ID set to $highestEventId")
    }
}
