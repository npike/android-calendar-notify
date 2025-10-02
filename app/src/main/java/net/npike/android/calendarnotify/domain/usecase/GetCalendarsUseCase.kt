package net.npike.android.calendarnotify.domain.usecase

import kotlinx.coroutines.flow.Flow
import net.npike.android.calendarnotify.data.repository.CalendarRepository
import net.npike.android.calendarnotify.domain.model.Calendar
import javax.inject.Inject

class GetCalendarsUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
) {

    operator fun invoke(): Flow<List<Calendar>> {
        return calendarRepository.getSystemCalendars()
    }
}