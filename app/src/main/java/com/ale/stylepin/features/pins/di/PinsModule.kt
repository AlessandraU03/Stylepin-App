package com.ale.stylepin.features.pins.di

import com.ale.stylepin.core.di.AppContainer
import com.ale.stylepin.features.pins.data.repositories.PinRepositoryImpl
import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import com.ale.stylepin.features.pins.domain.usecases.GetPinsUseCase
import com.ale.stylepin.features.pins.domain.usecases.AddPinsUseCase
import com.ale.stylepin.features.pins.presentation.viewmodels.PinsViewModelFactory
import com.ale.stylepin.features.pins.presentation.viewmodels.AddPinViewModelFactory
import com.ale.stylepin.features.pins.domain.usecases.DeletePinsUseCase

class PinModule(private val appContainer: AppContainer) {
    // CORRECCIÓN: Añadimos el tipo : PinsRepository explícitamente
    private val pinsRepository: PinsRepository by lazy<PinsRepository> {
        PinRepositoryImpl(appContainer.stylePinApi)
    }

    private val getPinsUseCase by lazy { GetPinsUseCase(pinsRepository) }
    // Dentro de tu clase PinModule
    private val deletePinsUseCase by lazy { DeletePinsUseCase(pinsRepository) }

    // Si tu ViewModel necesita ambos, asegúrate de pasárselo a la Factory
    fun providePinsViewModelFactory() = PinsViewModelFactory(
        getPinsUseCase,
        deletePinsUseCase // Inyectamos el nuevo caso de uso
    )
    // Asegúrate de que el nombre sea AddPinsUseCase (con S) si así llamaste al archivo
    private val addPinsUseCase by lazy { AddPinsUseCase(pinsRepository) }


    // Esta función es vital para el PinsNavGraph
    fun provideAddPinViewModelFactory() = AddPinViewModelFactory(addPinsUseCase)
}