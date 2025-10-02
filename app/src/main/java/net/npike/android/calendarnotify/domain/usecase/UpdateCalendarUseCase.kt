package net.npike.android.calendarnotify.domain.usecase

import net.npike.android.calendarnotify.data.repository.CalendarRepository
import javax.inject.Inject

class UpdateCalendarUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
) {
    suspend operator fun invoke(calendarId: String, isMonitored: Boolean) {
        calendarRepository.updateCalendarMonitoring(calendarId, isMonitored)
    }
}