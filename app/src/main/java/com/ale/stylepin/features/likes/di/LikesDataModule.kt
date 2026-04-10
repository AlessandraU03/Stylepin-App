package com.ale.stylepin.features.likes.di

import com.ale.stylepin.features.likes.data.repository.LikeRepositoryImpl
import com.ale.stylepin.features.likes.domain.repository.LikeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LikesDataModule {

    @Binds
    @Singleton
    abstract fun bindLikeRepository(
        likeRepositoryImpl: LikeRepositoryImpl
    ): LikeRepository
}
