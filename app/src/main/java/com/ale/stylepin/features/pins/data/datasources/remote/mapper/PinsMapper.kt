package com.ale.stylepin.features.pins.data.datasources.remote.mapper

import com.ale.stylepin.features.pins.data.datasources.remote.model.PinDto
import com.ale.stylepin.features.pins.data.datasources.remote.model.UpdatePinDto
import com.ale.stylepin.features.pins.domain.entities.Pin
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

// ── API Response → Domain ─────────────────────────────────────

fun PinDto.toDomain(): Pin = Pin(
    // 👇 SOLUCIÓN: Agregamos ?: "" para proteger de nulos
    id = id ?: "",
    userId = user_id ?: "",
    username = user_username?.takeIf { it.isNotBlank() } ?: "usuario",
    userFullName = user_full_name?.takeIf { it.isNotBlank() }
        ?: user_username?.takeIf { it.isNotBlank() }
        ?: "Usuario Anónimo",
    userAvatarUrl = user_avatar_url ?: "",
    userIsVerified = user_is_verified ?: false,
    imageUrl = image_url ?: "",
    title = title ?: "",
    description = description ?: "",
    category = category ?: "Sin categoría",
    styles = styles ?: emptyList(),
    occasions = occasions ?: emptyList(),
    season = season ?: "todo_el_ano",
    brands = brands ?: emptyList(),
    priceRange = price_range ?: "bajo_500",
    whereToBuy = where_to_buy,
    purchaseLink = purchase_link,
    likesCount = likes_count ?: 0,
    savesCount = saves_count ?: 0,
    commentsCount = comments_count ?: 0,
    viewsCount = views_count ?: 0,
    colors = colors ?: emptyList(),
    tags = tags ?: emptyList(),
    isPrivate = is_private ?: false,
    createdAt = created_at ?: "",
    updatedAt = updated_at ?: "",
    isLikedByMe = is_liked_by_me ?: false,
    isSavedByMe = is_saved_by_me ?: false
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
    styles: List<String>,
    occasions: List<String>,
    brands: List<String>,
    priceRange: String,
    whereToBuy: String?,
    purchaseLink: String?,
    colors: List<String>,
    tags: List<String>
): UpdatePinDto = UpdatePinDto(
    pinId = pinId,
    title = title,
    description = description,
    category = category,
    season = season,
    isPrivate = isPrivate,
    imageUrl = imageUrl,
    styles = styles,
    occasions = occasions,
    brands = brands,
    priceRange = priceRange,
    whereToBuy = whereToBuy,
    purchaseLink = purchaseLink,
    colors = colors,
    tags = tags
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