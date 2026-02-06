package com.ale.stylepin.features.auth.di

import com.ale.stylepin.core.di.AppContainer
import com.ale.stylepin.features.auth.presentation.viewmodels.LoginViewModelFactory
import com.ale.stylepin.features.auth.presentation.viewmodels.RegisterViewModelFactory // Nueva importación
import com.ale.stylepin.features.auth.domain.usecases.LoginUseCase
import com.ale.stylepin.features.auth.domain.usecases.RegisterUseCase // Nueva importación

class AuthModule(private val appContainer: AppContainer) {

    private val loginUseCase by lazy {
        LoginUseCase(appContainer.authRepository)
    }

    private val registerUseCase by lazy {
        RegisterUseCase(appContainer.authRepository)
    }

    fun provideLoginViewModelFactory(): LoginViewModelFactory {
        return LoginViewModelFactory(loginUseCase)
    }

    fun provideRegisterViewModelFactory(): RegisterViewModelFactory {
        return RegisterViewModelFactory(registerUseCase)
    }
}