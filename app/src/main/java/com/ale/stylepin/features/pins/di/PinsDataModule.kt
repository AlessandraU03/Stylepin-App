package com.ale.stylepin.features.pins.di

import com.ale.stylepin.features.pins.data.repositories.PinRepositoryImpl
import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PinsDataModule {
    @Binds
    abstract fun bindPinsRepository(
        pinRepositoryImpl: PinRepositoryImpl
    ): PinsRepository
}