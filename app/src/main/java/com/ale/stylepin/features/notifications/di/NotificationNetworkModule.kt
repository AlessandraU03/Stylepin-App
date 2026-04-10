package com.ale.stylepin.features.notifications.di

import com.ale.stylepin.core.di.StylePinRetrofit
import com.ale.stylepin.features.notifications.data.datasources.remote.api.NotificationApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationNetworkModule {

    @Provides
    @Singleton
    fun provideNotificationApi(@StylePinRetrofit retrofit: Retrofit): NotificationApi {
        return retrofit.create(NotificationApi::class.java)
    }
}