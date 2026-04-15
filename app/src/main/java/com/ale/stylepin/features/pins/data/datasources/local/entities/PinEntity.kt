package com.ale.stylepin.features.pins.data.datasources.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pins")
data class PinEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val username: String,
    val userFullName: String,
    val userAvatarUrl: String?,
    val userIsVerified: Boolean = false,
    val imageUrl: String,
    val title: String,
    val description: String?,
    val category: String,
    val styles: List<String>,
    val occasions: List<String>,
    val season: String,
    val brands: List<String>,
    val priceRange: String,
    val whereToBuy: String?,
    val purchaseLink: String?,
    val likesCount: Int,
    val savesCount: Int,
    val commentsCount: Int,
    val viewsCount: Int,
    val colors: List<String>,
    val tags: List<String>,
    val isPrivate: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val isLikedByMe: Boolean,
    val isSavedByMe: Boolean,
    val fetchedAt: Long = System.currentTimeMillis()
)
