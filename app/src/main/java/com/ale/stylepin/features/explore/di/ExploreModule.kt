package com.ale.stylepin.features.explore.di

import com.ale.stylepin.core.di.StylePinRetrofit
import com.ale.stylepin.features.explore.data.datasources.remote.api.ExploreApi
import com.ale.stylepin.features.explore.data.repositories.ExploreRepositoryImpl
import com.ale.stylepin.features.explore.domain.repository.ExploreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExploreModule {

    @Provides
    @Singleton
    fun provideExploreApi(@StylePinRetrofit retrofit: Retrofit): ExploreApi {
        return retrofit.create(ExploreApi::class.java)
    }

    @Provides
    @Singleton
    fun provideExploreRepository(api: ExploreApi): ExploreRepository {
        return ExploreRepositoryImpl(api)
    }
}