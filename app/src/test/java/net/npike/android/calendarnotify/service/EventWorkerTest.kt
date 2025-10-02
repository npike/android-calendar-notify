package net.npike.android.calendarnotify.service

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.npike.android.calendarnotify.domain.usecase.CheckForNewEventsUseCase
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventWorkerTest {

    private val mockCheckForNewEventsUseCase: CheckForNewEventsUseCase = mockk(relaxed = true)
    private val mockWorkManagerInitializer: WorkManagerInitializer = mockk(relaxed = true)
    private lateinit var context: Context

    @Before
    fun setup() {
        context = androidx.test.core.app.ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `doWork calls CheckForNewEventsUseCase and returns success`() = runBlocking {
        // Arrange
        coEvery { mockCheckForNewEventsUseCase.invoke() } returns Unit
        val worker = TestListenableWorkerBuilder<EventWorker>(context)
            .setWorkerFactory(object : WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker {
                    return EventWorker(
                        appContext,
                        workerParameters,
                        mockCheckForNewEventsUseCase,
                        mockWorkManagerInitializer
                    )
                }
            })
            .build()

        // Act
        val result = worker.doWork()

        // Assert
        coVerify(exactly = 1) { mockCheckForNewEventsUseCase.invoke() }
        assertTrue(result is ListenableWorker.Result.Success)
    }
}
