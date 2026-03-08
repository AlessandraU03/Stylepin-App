package com.ale.stylepin.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object RegisterRoute

@Serializable
object PinsRoute

@Serializable
object AddPinRoute
@Serializable
data class EditPinRoute(
    val id: String,
    val title: String,
    val imageUrl: String,
    val category: String,
    val season: String
)

