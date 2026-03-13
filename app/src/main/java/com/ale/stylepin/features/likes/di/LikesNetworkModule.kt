package com.ale.stylepin.features.likes.di

import com.ale.stylepin.core.di.StylePinRetrofit
import com.ale.stylepin.features.likes.data.datasources.remote.api.LikeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LikesNetworkModule {

    @Provides
    @Singleton
    fun provideLikeApi(@StylePinRetrofit retrofit: Retrofit): LikeApi {
        return retrofit.create(LikeApi::class.java)
    }
}
