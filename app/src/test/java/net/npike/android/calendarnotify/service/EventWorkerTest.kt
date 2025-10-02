package net.npike.android.calendarnotify.service

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import net.npike.android.calendarnotify.data.repository.CalendarRepository
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertTrue

class EventWorkerTest {

    @MockK
    lateinit var mockCalendarRepository: CalendarRepository

    @MockK
    lateinit var mockNotificationHelper: NotificationHelper

    private lateinit var context: Context

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        context = androidx.test.core.app.ApplicationProvider.getApplicationContext()

        // Mock the behavior of the repository and notification helper
        coEvery { mockCalendarRepository.fetchAndStoreEventsForCalendar(any(), any(), any()) } returns Unit
        coEvery { mockCalendarRepository.updateEventSeenStatus(any(), any()) } returns Unit
        coEvery { mockNotificationHelper.createForegroundServiceNotification() } returns mockk()
    }

    @Test
    fun `doWork returns Result success`() = runBlocking {
        val worker = TestListenableWorkerBuilder<EventWorker>(context)
            .setWorkerFactory(object : WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker? {
                    return EventWorker(
                        appContext,
                        workerParameters,
                        mockCalendarRepository,
                        mockNotificationHelper
                    )
                }
            })
            .build()

        val result = worker.doWork()
        assertTrue(result is ListenableWorker.Result.Success)
    }
}