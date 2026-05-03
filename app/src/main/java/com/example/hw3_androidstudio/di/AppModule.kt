package com.example.hw3_androidstudio.di

import com.example.hw3_androidstudio.data.api.RetrofitProvider
import com.example.hw3_androidstudio.data.api.SwapiApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApi(): SwapiApi {
        return RetrofitProvider.api
    }
}