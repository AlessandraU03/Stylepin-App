package com.ale.stylepin.features.pins.data.datasources.remote.mapper

import com.ale.stylepin.features.pins.data.datasources.remote.model.*
import com.ale.stylepin.features.pins.domain.entities.Comment
import com.ale.stylepin.features.pins.domain.entities.Pin
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

fun PinDto.toDomain(): Pin = Pin(
    id = id, userId = user_id, username = user_username, userFullName = user_full_name,
    userAvatarUrl = user_avatar_url, userIsVerified = user_is_verified, imageUrl = image_url,
    title = title, description = description, category = category, styles = styles,
    occasions = occasions, season = season, brands = brands, priceRange = price_range,
    whereToBuy = where_to_buy, purchaseLink = purchase_link, likesCount = likes_count,
    savesCount = saves_count, commentsCount = comments_count, viewsCount = views_count,
    colors = colors, tags = tags, isPrivate = is_private, createdAt = created_at,
    updatedAt = updated_at, isLikedByMe = is_liked_by_me, isSavedByMe = is_saved_by_me
)

// MAPEA EL DTO DE RED A LA ENTIDAD DE DOMINIO QUE LA UI ENTIENDE
fun CommentDto.toDomain(): Comment = Comment(
    id = id, userId = userId, userUsername = userUsername, userFullName = userFullName,
    userAvatarUrl = userAvatarUrl, text = text, createdAt = createdAt
)

fun toUpdateRequest(
    pinId: String, title: String, description: String?, category: String,
    season: String, isPrivate: Boolean, imageUrl: String?
): UpdatePinDto = UpdatePinDto(pinId, title, imageUrl, category, season, description, isPrivate)

fun buildPartMap(
    title: String, category: String, season: String, description: String?,
    isPrivate: Boolean, styles: List<String>, occasions: List<String>, brands: List<String>,
    priceRange: String, whereToBuy: String?, purchaseLink: String?, colors: List<String>, tags: List<String>
): Map<String, RequestBody> {
    val plain = "text/plain".toMediaTypeOrNull()
    val fields = mutableMapOf<String, RequestBody>()
    fields["title"] = title.toRequestBody(plain)
    fields["category"] = category.ifBlank { "outfit_completo" }.toRequestBody(plain)
    fields["season"] = season.ifBlank { "todo_el_ano" }.toRequestBody(plain)
    fields["price_range"] = priceRange.ifBlank { "bajo_500" }.toRequestBody(plain)
    fields["is_private"] = isPrivate.toString().toRequestBody(plain)

    if (!description.isNullOrBlank()) fields["description"] = description.toRequestBody(plain)
    if (!whereToBuy.isNullOrBlank()) fields["where_to_buy"] = whereToBuy.toRequestBody(plain)
    if (!purchaseLink.isNullOrBlank()) fields["purchase_link"] = purchaseLink.toRequestBody(plain)

    if (styles.isNotEmpty()) fields["styles"] = styles.joinToString(",", "[", "]").toRequestBody(plain)
    if (occasions.isNotEmpty()) fields["occasions"] = occasions.joinToString(",", "[", "]").toRequestBody(plain)
    if (brands.isNotEmpty()) fields["brands"] = brands.joinToString(",", "[", "]").toRequestBody(plain)
    if (colors.isNotEmpty()) fields["colors"] = colors.joinToString(",", "[", "]").toRequestBody(plain)
    if (tags.isNotEmpty()) fields["tags"] = tags.joinToString(",", "[", "]").toRequestBody(plain)

    return fields
}