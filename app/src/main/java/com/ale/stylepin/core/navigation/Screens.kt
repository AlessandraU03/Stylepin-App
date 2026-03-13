package com.ale.stylepin.core.navigation

import kotlinx.serialization.Serializable

@Serializable object LoginRoute
@Serializable object RegisterRoute
@Serializable object PinsRoute
@Serializable object SearchRoute
@Serializable object AlertsRoute
@Serializable object ProfileRoute
@Serializable object EditProfileRoute
@Serializable object AddPinRoute
@Serializable data class PinDetailRoute(val id: String)
@Serializable data class EditPinRoute(val id: String)
@Serializable object SettingsRoute
@Serializable data class CommunityRoute(val initialTab: Int = 0, val userId: String)
@Serializable object BoardsRoute
@Serializable data class BoardDetailRoute(val id: String)
@Serializable data class CreateBoardRoute(val userId: String)
@Serializable data class EditBoardRoute(val id: String)
@Serializable data class PublicProfileRoute(val userId: String)