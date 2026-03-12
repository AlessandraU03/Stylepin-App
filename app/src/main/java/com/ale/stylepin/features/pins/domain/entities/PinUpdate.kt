package com.ale.stylepin.features.pins.domain.entities

/**
 * Entidad de dominio que representa los datos para actualizar un pin.
 * Solo incluye los campos editables.
 */
data class PinUpdate(
    val pinId: String,
    val title: String,
    val imageUrl: String? = null,
    val category: String,
    val season: String,
    val description: String? = null,
    val isPrivate: Boolean = false
)