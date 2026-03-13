package com.ale.stylepin.core.di

import com.ale.stylepin.core.hardware.data.AndroidBiometricManager
import com.ale.stylepin.core.hardware.data.AndroidFlashManager
import com.ale.stylepin.core.hardware.domain.BiometricManager
import com.ale.stylepin.core.hardware.domain.FlashManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HardwareModule {

    @Binds
    @Singleton
    abstract fun bindFlashManager(
        impl: AndroidFlashManager
    ): FlashManager

    @Binds
    @Singleton
    abstract fun bindBiometricManager(
        impl: AndroidBiometricManager
    ): BiometricManager
}
