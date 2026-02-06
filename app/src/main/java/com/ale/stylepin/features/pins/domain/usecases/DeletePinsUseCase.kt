package com.ale.stylepin.features.pins.domain.usecases

import com.ale.stylepin.features.pins.domain.repository.PinsRepository

class DeletePinsUseCase(private val repository: PinsRepository) {

    /**
     * Ejecuta la eliminación de un pin específico.
     * @param pinId El identificador único del pin en la base de datos de EC2.
     * @return true si la eliminación fue exitosa (204 No Content), false en caso contrario.
     */
    suspend fun execute(pinId: String): Boolean {
        return repository.deletePin(pinId)
    }
}