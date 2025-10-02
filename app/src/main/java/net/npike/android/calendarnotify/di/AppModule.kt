package net.npike.android.calendarnotify.di

import android.content.Context
import android.content.ContentResolver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.npike.android.calendarnotify.data.local.DataStoreManager
import net.npike.android.calendarnotify.data.local.EventDao
import net.npike.android.calendarnotify.data.repository.CalendarRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context) = context.contentResolver

    @Provides
    @Singleton
    fun provideCalendarRepository(
        @ApplicationContext context: Context,
        contentResolver: ContentResolver,
        eventDao: EventDao,
        dataStoreManager: DataStoreManager
    ): CalendarRepository {
        return CalendarRepository(context, contentResolver, eventDao, dataStoreManager)
    }
}