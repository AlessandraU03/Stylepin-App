package com.ale.stylepin.features.pins.di

import com.ale.stylepin.core.di.StylePinRetrofit
import com.ale.stylepin.features.pins.data.datasources.remote.api.PinApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PinsNetworkModule {

    @Provides
    @Singleton
    fun providePinApi(@StylePinRetrofit retrofit: Retrofit): PinApi {
        return retrofit.create(PinApi::class.java)
    }
}
