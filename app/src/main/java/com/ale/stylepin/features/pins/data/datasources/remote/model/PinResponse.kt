package com.ale.stylepin.features.pins.data.datasources.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class PinResponse(
    val id: String,
    val user_id: String,
    val user_username: String,
    val user_full_name: String,
    val user_avatar_url: String? = null,
    val user_is_verified: Boolean,
    val image_url: String,
    val title: String,
    val description: String? = null,
    val category: String,
    val styles: List<String> = emptyList(),
    val occasions: List<String> = emptyList(),
    val season: String,
    val brands: List<String> = emptyList(),
    val price_range: String,
    val where_to_buy: String? = null,
    val purchase_link: String? = null,
    val likes_count: Int = 0,
    val saves_count: Int = 0,
    val comments_count: Int = 0,
    val views_count: Int = 0,
    val colors: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val is_private: Boolean = false,
    val created_at: String,
    val updated_at: String,
    val is_liked_by_me: Boolean = false,
    val is_saved_by_me: Boolean = false
)
