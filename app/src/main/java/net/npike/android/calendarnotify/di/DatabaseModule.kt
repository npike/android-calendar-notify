package net.npike.android.calendarnotify.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.npike.android.calendarnotify.data.local.AppDatabase
import net.npike.android.calendarnotify.data.local.EventDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "calendar-notify-db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideEventDao(database: AppDatabase): EventDao {
        return database.eventDao()
    }
}