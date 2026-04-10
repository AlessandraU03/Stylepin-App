package com.ale.stylepin.features.community.data.datasources.remote.model

data class FollowStatusResponse(
    val is_following: Boolean,
    val is_followed_by: Boolean,
    val are_mutual: Boolean
)