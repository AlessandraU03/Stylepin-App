package com.ale.stylepin.features.boards.di

import com.ale.stylepin.features.boards.data.repositories.BoardRepositoryImpl
import com.ale.stylepin.features.boards.domain.repository.BoardsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BoardsModule {
    @Binds
    abstract fun bindBoardsRepository(
        impl: BoardRepositoryImpl
    ): BoardsRepository
}