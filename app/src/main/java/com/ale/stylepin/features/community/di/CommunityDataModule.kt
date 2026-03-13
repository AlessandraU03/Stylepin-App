package com.ale.stylepin.features.community.di

import com.ale.stylepin.features.community.data.repositories.CommunityRepositoryImpl
import com.ale.stylepin.features.community.domain.repositories.CommunityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class CommunityDataModule {
    @Binds
    @Singleton
    abstract fun bindCommunityRepository(
        communityRepositoryImpl: CommunityRepositoryImpl
    ): CommunityRepository
}