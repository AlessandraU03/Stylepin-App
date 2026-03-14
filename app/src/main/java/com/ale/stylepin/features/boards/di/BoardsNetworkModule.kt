package com.ale.stylepin.features.pins.di

import com.ale.stylepin.core.di.StylePinRetrofit
import com.ale.stylepin.features.boards.data.datasources.remote.api.BoardApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BoardsNetworkModule {

    @Provides
    @Singleton
    fun provideBoardApi(@StylePinRetrofit retrofit: Retrofit): BoardApi {
        return retrofit.create(BoardApi::class.java)
    }
}
