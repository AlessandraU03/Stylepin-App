package com.ale.stylepin.features.pins.data.datasources.remote.model

import kotlinx.serialization.Serializable

// ── Responses ─────────────────────────────────────────────

@Serializable
data class PinDto(
    val id: String,
    val user_id: String,
    val user_username: String,
    val user_full_name: String,
    val user_avatar_url: String? = null,
    val user_is_verified: Boolean = false,
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

@Serializable
data class PinsListResponse(
    val pins: List<PinDto>,
    val total: Int = 0,
    val limit: Int = 20,
    val offset: Int = 0,
    val has_more: Boolean = false
)

// ── Request ───────────────────────────────────────────────

@Serializable
data class UpdatePinDto(
    val pinId: String,
    val title: String,
    val imageUrl: String?,
    val category: String,
    val season: String,
    val description: String?,
    val isPrivate: Boolean
)

@Serializable
data class PinsFeedDto(
    val pins: List<PinDto>,
    val limit: Int,
    val offset: Int,
    val has_more: Boolean
)

@Serializable
data class AddPinDto(
    val title: String,
    val imageUrl: String,
    val category: String,
    val season: String,
    val description: String?,
    val isPrivate: Boolean,
    val styles: List<String>,
    val occasions: List<String>,
    val brands: List<String>,
    val priceRange: String,
    val whereToBuy: String?,
    val purchaseLink: String?,
    val colors: List<String>,
    val tags: List<String>
)


@Serializable
data class PinsTrendingDto(
    val pins: List<PinDto>,
    val hours: Int
)

data class MessageResponse(
    val message: String
)
