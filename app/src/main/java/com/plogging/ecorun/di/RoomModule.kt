package com.plogging.ecorun.di

import android.content.Context
import androidx.room.Room
import com.plogging.ecorun.data.local.PloggingDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): PloggingDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            PloggingDatabase::class.java, "Ecorun.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMyPloggingDao(db: PloggingDatabase) = db.myPloggingDao()

    @Provides
    fun providePloggingKeyDao(db: PloggingDatabase) = db.ploggingKeysDao()
}