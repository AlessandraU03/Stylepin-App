package com.ale.stylepin.core.di

import android.content.Context
import androidx.room.Room
import com.ale.stylepin.core.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "stylepin_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun providePinDao(db: AppDatabase) = db.pinDao()
}
