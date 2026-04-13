package com.ale.stylepin.features.boards.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

// ── Responses ─────────────────────────────────────────────
// IMPORTANTE: NO usar @Serializable (kotlinx) porque Retrofit usa GsonConverterFactory.
// Gson ignora @Serializable y falla silenciosamente si un campo requerido viene null.
// Todos los campos tienen valores por defecto para que Gson siempre pueda construir el objeto.

data class BoardDto(
    @SerializedName("id")               val id: String = "",
    @SerializedName("user_id")          val user_id: String = "",
    @SerializedName("user_username")    val user_username: String = "",
    @SerializedName("user_full_name")   val user_full_name: String? = null,
    @SerializedName("user_avatar_url")  val user_avatar_url: String? = null,
    @SerializedName("name")             val name: String = "",
    @SerializedName("description")      val description: String? = null,
    @SerializedName("cover_image_url")  val cover_image_url: String? = null,
    @SerializedName("is_private")       val is_private: Boolean = false,
    @SerializedName("is_collaborative") val is_collaborative: Boolean = false,
    @SerializedName("pins_count")       val pins_count: Int = 0,
    @SerializedName("created_at")       val created_at: String = "",
    @SerializedName("updated_at")       val updated_at: String? = null,
    @SerializedName("is_owner")         val is_owner: Boolean = false,
    @SerializedName("is_collaborator")  val is_collaborator: Boolean = false
)

data class BoardListResponse(
    @SerializedName("boards")   val boards: List<BoardDto> = emptyList(),
    @SerializedName("total")    val total: Int = 0,
    @SerializedName("limit")    val limit: Int = 20,
    @SerializedName("offset")   val offset: Int = 0,
    @SerializedName("has_more") val has_more: Boolean = false
)

data class BoardPinResponse(
    @SerializedName("id")         val id: String = "",
    @SerializedName("board_id")   val board_id: String = "",
    @SerializedName("pin_id")     val pin_id: String = "",
    @SerializedName("user_id")    val user_id: String = "",
    @SerializedName("notes")      val notes: String? = null,
    @SerializedName("created_at") val created_at: String = ""
)

data class BoardPinListResponse(
    @SerializedName("pins")     val pins: List<BoardPinResponse> = emptyList(),
    @SerializedName("total")    val total: Int = 0,
    @SerializedName("limit")    val limit: Int = 20,
    @SerializedName("offset")   val offset: Int = 0,
    @SerializedName("has_more") val has_more: Boolean = false
)

data class BoardCollaboratorResponse(
    @SerializedName("id")              val id: String = "",
    @SerializedName("board_id")        val board_id: String = "",
    @SerializedName("user_id")         val user_id: String = "",
    @SerializedName("user_username")   val user_username: String = "",
    @SerializedName("user_full_name")  val user_full_name: String = "",
    @SerializedName("user_avatar_url") val user_avatar_url: String? = null,
    @SerializedName("can_edit")        val can_edit: Boolean = false,
    @SerializedName("can_add_pins")    val can_add_pins: Boolean = true,
    @SerializedName("can_remove_pins") val can_remove_pins: Boolean = false,
    @SerializedName("created_at")      val created_at: String = ""
)

data class CollaboratorListResponse(
    @SerializedName("collaborators") val collaborators: List<BoardCollaboratorResponse> = emptyList()
)

// ── Requests ──────────────────────────────────────────────
// Gson serializa estos objetos usando @SerializedName para que el JSON
// tenga los nombres snake_case que espera la API Python.

data class CreateBoardRequest(
    @SerializedName("name")             val name: String,
    @SerializedName("description")      val description: String? = null,
    @SerializedName("is_private")       val is_private: Boolean = false,
    @SerializedName("is_collaborative") val is_collaborative: Boolean = false
)

data class UpdateBoardRequest(
    @SerializedName("name")             val name: String,
    @SerializedName("description")      val description: String? = null,
    @SerializedName("is_private")       val is_private: Boolean = false,
    @SerializedName("is_collaborative") val is_collaborative: Boolean = false,
    @SerializedName("cover_image_url")  val cover_image_url: String? = null
)

data class AddPinToBoardRequest(
    @SerializedName("pin_id") val pin_id: String,
    @SerializedName("notes")  val notes: String? = null
)

data class AddCollaboratorRequest(
    @SerializedName("user_id")         val user_id: String,
    @SerializedName("can_edit")        val can_edit: Boolean = false,
    @SerializedName("can_add_pins")    val can_add_pins: Boolean = true,
    @SerializedName("can_remove_pins") val can_remove_pins: Boolean = false
)

data class UpdateCollaboratorRequest(
    @SerializedName("can_edit")        val can_edit: Boolean = false,
    @SerializedName("can_add_pins")    val can_add_pins: Boolean = true,
    @SerializedName("can_remove_pins") val can_remove_pins: Boolean = false
)