package com.ale.stylepin.features.explore.data.datasources.remote.mapper

import com.ale.stylepin.features.explore.data.datasources.remote.model.PinSearchSummaryDto
import com.ale.stylepin.features.explore.data.datasources.remote.model.UserSearchDto
import com.ale.stylepin.features.explore.domain.entities.UserSearchResult
import com.ale.stylepin.features.pins.domain.entities.Pin

fun UserSearchDto.toDomain(): UserSearchResult = UserSearchResult(
    id = id,
    username = username,
    fullName = fullName?.takeIf { it.isNotBlank() } ?: username,
    avatarUrl = avatarUrl ?: "",
    isVerified = isVerified ?: false
)

// Convertimos el Pin recortado de la búsqueda a un Pin completo rellenando lo faltante
fun PinSearchSummaryDto.toDomainPin(): Pin = Pin(
    id = id,
    userId = userId ?: "",
    username = userUsername ?: "usuario",
    userFullName = userUsername ?: "Usuario Anónimo",
    userAvatarUrl = userAvatarUrl ?: "",
    userIsVerified = false,
    imageUrl = imageUrl ?: "",
    title = title ?: "",
    description = "", // La búsqueda no lo devuelve
    category = category ?: "Sin categoría",
    styles = emptyList(), // La búsqueda no lo devuelve
    occasions = emptyList(),
    season = "todo_el_ano",
    brands = emptyList(),
    priceRange = "bajo_500",
    whereToBuy = null,
    purchaseLink = null,
    likesCount = likesCount ?: 0,
    savesCount = savesCount ?: 0,
    commentsCount = 0,
    viewsCount = 0,
    colors = emptyList(),
    tags = emptyList(),
    isPrivate = false,
    createdAt = createdAt ?: "",
    updatedAt = "",
    isLikedByMe = false,
    isSavedByMe = false
)