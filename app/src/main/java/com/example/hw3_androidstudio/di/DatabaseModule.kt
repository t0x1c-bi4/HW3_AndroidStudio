package com.example.hw3_androidstudio.di

import android.content.Context
import androidx.room.Room
import com.example.hw3_androidstudio.data.local.AppDatabase
import com.example.hw3_androidstudio.data.local.FavouriteDao
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
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_db"
        ).build()
    }

    @Provides
    fun provideDao(db: AppDatabase): FavouriteDao =
        db.favouriteDao()
}