package com.ale.stylepin.features.auth.di

import com.ale.stylepin.core.di.AppContainer
import com.ale.stylepin.features.auth.presentation.viewmodels.LoginViewModelFactory
import com.ale.stylepin.features.auth.domain.usecases.LoginUseCase

class AuthModule(private val appContainer: AppContainer) {

    private val loginUseCase by lazy {
        LoginUseCase(appContainer.authRepository)
    }

    fun provideLoginViewModelFactory(): LoginViewModelFactory {
        return LoginViewModelFactory(loginUseCase)
    }
}