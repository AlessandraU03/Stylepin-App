package com.ale.stylepin.features.profile.di

import com.ale.stylepin.core.di.StylePinRetrofit
import com.ale.stylepin.features.profile.data.datasources.remote.api.ProfileApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileApiModule {

    @Provides
    @Singleton
    fun provideProfileApi(@StylePinRetrofit retrofit: Retrofit): ProfileApi {
        return retrofit.create(ProfileApi::class.java)
    }
}
