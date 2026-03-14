package com.ale.stylepin.features.pins.data.datasources.remote.mapper

import com.ale.stylepin.features.pins.data.datasources.remote.model.PinDto
import com.ale.stylepin.features.pins.data.datasources.remote.model.UpdatePinDto
import com.ale.stylepin.features.pins.domain.entities.Pin
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

// ── API Response → Domain ─────────────────────────────────────

fun PinDto.toDomain(): Pin = Pin(
    id = id,
    userId = user_id,
    username = user_username,
    userFullName = user_full_name,
    userAvatarUrl = user_avatar_url,
    userIsVerified = user_is_verified,
    imageUrl = image_url,
    title = title,
    description = description,
    category = category,
    styles = styles,
    occasions = occasions,
    season = season,
    brands = brands,
    priceRange = price_range,
    whereToBuy = where_to_buy,
    purchaseLink = purchase_link,
    likesCount = likes_count,
    savesCount = saves_count,
    commentsCount = comments_count,
    viewsCount = views_count,
    colors = colors,
    tags = tags,
    isPrivate = is_private,
    createdAt = created_at,
    updatedAt = updated_at,
    isLikedByMe = is_liked_by_me,
    isSavedByMe = is_saved_by_me
)

// ── Domain params → UpdatePinRequest ─────────────────────────

fun toUpdateRequest(
    pinId: String,
    title: String,
    description: String?,
    category: String,
    season: String,
    isPrivate: Boolean,
    imageUrl: String?,
): UpdatePinDto = UpdatePinDto(
    pinId = pinId,
    title = title,
    description = description,
    category = category,
    season = season,
    isPrivate = isPrivate,
    imageUrl = imageUrl
)

// ── Multipart map para crear pin ──────────────────────────────

fun buildPartMap(
    title: String,
    category: String,
    season: String,
    description: String?,
    isPrivate: Boolean,
    styles: List<String>,
    occasions: List<String>,
    brands: List<String>,
    priceRange: String,
    whereToBuy: String?,
    purchaseLink: String?,
    colors: List<String>,
    tags: List<String>
): Map<String, RequestBody> {
    val plain = "text/plain".toMediaTypeOrNull()
    return buildMap {
        put("title", title.toRequestBody(plain))
        put("category", category.toRequestBody(plain))
        put("season", season.ifBlank { "todo_el_ano" }.toRequestBody(plain))
        put("description", (description ?: "").toRequestBody(plain))
        put("is_private", isPrivate.toString().toRequestBody(plain))
        put("price_range", priceRange.ifBlank { "bajo_500" }.toRequestBody(plain))
        put("styles", styles.joinToString(",", "[", "]").toRequestBody(plain))
        put("occasions", occasions.joinToString(",", "[", "]").toRequestBody(plain))
        put("brands", brands.joinToString(",", "[", "]").toRequestBody(plain))
        put("colors", colors.joinToString(",", "[", "]").toRequestBody(plain))
        put("tags", tags.joinToString(",", "[", "]").toRequestBody(plain))
        put("where_to_buy", (whereToBuy ?: "").toRequestBody(plain))
        put("purchase_link", (purchaseLink ?: "").toRequestBody(plain))
    }
}
