package com.ale.stylepin.features.pins.data.datasources.remote.mapper

import com.ale.stylepin.features.pins.data.datasources.remote.model.AddPinRequest
import com.ale.stylepin.features.pins.data.datasources.remote.model.PinResponse
import com.ale.stylepin.features.pins.data.datasources.remote.model.UpdatePinRequest
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.entities.PinCreate
import com.ale.stylepin.features.pins.domain.entities.PinUpdate
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

// ─────────────────────────────────────────────────────────────
// API Response → Domain Entity
// ─────────────────────────────────────────────────────────────

fun PinResponse.toDomain(): Pin = Pin(
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

// ─────────────────────────────────────────────────────────────
// Domain Entity → Data DTO
// Estas conversiones viven en data, no en dominio,
// para no contaminar las entidades con OkHttp.
// ─────────────────────────────────────────────────────────────

fun PinCreate.toCreateDto(): AddPinRequest = AddPinRequest(
    title = title,
    imageUrl = imageUrl,
    category = category,
    season = season,
    description = description,
    isPrivate = isPrivate,
    styles = styles,
    occasions = occasions,
    brands = brands,
    priceRange = priceRange,
    whereToBuy = whereToBuy,
    purchaseLink = purchaseLink,
    colors = colors,
    tags = tags
)

fun PinUpdate.toUpdateDto(): UpdatePinRequest = UpdatePinRequest(
    pinId = pinId,
    title = title,
    imageUrl = imageUrl,
    category = category,
    season = season,
    description = description,
    isPrivate = isPrivate
)

// ─────────────────────────────────────────────────────────────
// Data DTO → OkHttp RequestBody map
// La lógica de multipart vive aquí, lejos del repositorio.
// ─────────────────────────────────────────────────────────────

fun AddPinRequest.toPartMap(): Map<String, RequestBody> {
    val plainText = "text/plain".toMediaTypeOrNull()
    val map = mutableMapOf<String, RequestBody>()

    map["title"] = title.toRequestBody(plainText)
    map["category"] = category.toRequestBody(plainText)
    map["season"] = season.ifBlank { "todo_el_ano" }.toRequestBody(plainText)
    map["description"] = (description ?: "").toRequestBody(plainText)
    map["is_private"] = isPrivate.toString().toRequestBody(plainText)
    map["price_range"] = priceRange.ifBlank { "bajo_500" }.toRequestBody(plainText)
    map["styles"] = styles.joinToString(",", "[", "]").toRequestBody(plainText)
    map["occasions"] = occasions.joinToString(",", "[", "]").toRequestBody(plainText)
    map["brands"] = brands.joinToString(",", "[", "]").toRequestBody(plainText)
    map["colors"] = colors.joinToString(",", "[", "]").toRequestBody(plainText)
    map["tags"] = tags.joinToString(",", "[", "]").toRequestBody(plainText)
    map["where_to_buy"] = (whereToBuy ?: "").toRequestBody(plainText)
    map["purchase_link"] = (purchaseLink ?: "").toRequestBody(plainText)

    return map
}

