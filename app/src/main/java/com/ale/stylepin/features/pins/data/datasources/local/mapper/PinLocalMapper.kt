package com.ale.stylepin.features.pins.data.datasources.local.mapper

import com.ale.stylepin.features.pins.data.datasources.remote.model.PinDto
import com.ale.stylepin.features.pins.data.datasources.local.entities.PinEntity
import com.ale.stylepin.features.pins.domain.entities.Pin

fun PinDto.toEntity(): PinEntity = PinEntity(
    id = id,
    userId = user_id,
    username = user_username,
    userFullName = user_full_name,
    userAvatarUrl = user_avatar_url,
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

fun PinEntity.toDomain(): Pin = Pin(
    id = id,
    userId = userId,
    username = username,
    userFullName = userFullName,
    userAvatarUrl = userAvatarUrl,
    userIsVerified = false, // Opcional: podrías guardarlo en el entity si es necesario
    imageUrl = imageUrl,
    title = title,
    description = description,
    category = category,
    styles = styles,
    occasions = occasions,
    season = season,
    brands = brands,
    priceRange = priceRange,
    whereToBuy = whereToBuy,
    purchaseLink = purchaseLink,
    likesCount = likesCount,
    savesCount = savesCount,
    commentsCount = commentsCount,
    viewsCount = viewsCount,
    colors = colors,
    tags = tags,
    isPrivate = isPrivate,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isLikedByMe = isLikedByMe,
    isSavedByMe = isSavedByMe
)
