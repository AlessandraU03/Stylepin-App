package com.ale.stylepin.features.pins.data.datasources.remote.mapper

import com.ale.stylepin.features.pins.data.datasources.remote.model.PinResponse
import com.ale.stylepin.features.pins.domain.entities.Pin

fun PinResponse.toDomain(): Pin {
    return Pin(
        id = this.id,
        username = this.user_username,
        imageUrl = this.image_url,
        title = this.title,
        category = this.category ?: "General",
        occasions = this.occasions ?: emptyList(),
        likesCount = this.likes_count,
        season = this.season ?: "todo_el_ano"
    )
}