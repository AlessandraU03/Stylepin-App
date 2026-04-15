package com.ale.stylepin.features.community.di

import com.ale.stylepin.core.di.StylePinRetrofit
import com.ale.stylepin.features.community.data.datasources.remote.api.CommunityApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
object CommunityNetworkModule {
    @Provides
    @Singleton
    fun provideCommunityApi(@StylePinRetrofit retrofit: Retrofit): CommunityApi {
        return retrofit.create(CommunityApi::class.java)
    }
}