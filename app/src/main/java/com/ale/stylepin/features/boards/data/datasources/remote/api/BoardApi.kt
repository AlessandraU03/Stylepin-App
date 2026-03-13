package com.ale.stylepin.features.boards.data.datasources.remote.api

import com.ale.stylepin.features.boards.data.datasources.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface BoardApi {

    @GET("api/v1/boards")
    suspend fun getAllBoards(
        @Query("user_id") userId: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<List<BoardDto>>

    @GET("api/v1/boards/user/{user_id}")
    suspend fun getUserBoards(
        @Path("user_id") userId: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<List<BoardDto>>

    @GET("api/v1/boards/{board_id}")
    suspend fun getBoardById(
        @Path("board_id") boardId: String
    ): Response<BoardDto>

    @POST("api/v1/boards")
    suspend fun createBoard(
        @Body request: CreateBoardRequest
    ): Response<BoardDto>

    @PUT("api/v1/boards/{board_id}")
    suspend fun updateBoard(
        @Path("board_id") boardId: String,
        @Body request: UpdateBoardRequest
    ): Response<BoardDto>

    @DELETE("api/v1/boards/{board_id}")
    suspend fun deleteBoard(
        @Path("board_id") boardId: String
    ): Response<Unit>

    @GET("api/v1/boards/{board_id}/pins")
    suspend fun getBoardPins(
        @Path("board_id") boardId: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<BoardPinListResponse>

    @POST("api/v1/boards/{board_id}/pins")
    suspend fun addPinToBoard(
        @Path("board_id") boardId: String,
        @Body request: AddPinToBoardRequest
    ): Response<BoardPinResponse>

    @DELETE("api/v1/boards/{board_id}/pins/{pin_id}")
    suspend fun removePinFromBoard(
        @Path("board_id") boardId: String,
        @Path("pin_id") pinId: String
    ): Response<Unit>

    @GET("api/v1/boards/{board_id}/collaborators")
    suspend fun getCollaborators(
        @Path("board_id") boardId: String
    ): Response<CollaboratorListResponse>

    @POST("api/v1/boards/{board_id}/collaborators")
    suspend fun addCollaborator(
        @Path("board_id") boardId: String,
        @Body request: AddCollaboratorRequest
    ): Response<BoardCollaboratorResponse>

    @DELETE("api/v1/boards/{board_id}/collaborators/{collaborator_user_id}")
    suspend fun removeCollaborator(
        @Path("board_id") boardId: String,
        @Path("collaborator_user_id") collaboratorUserId: String
    ): Response<Unit>

    @PUT("api/v1/boards/{board_id}/collaborators/{collaborator_user_id}")
    suspend fun updateCollaboratorPermissions(
        @Path("board_id") boardId: String,
        @Path("collaborator_user_id") collaboratorUserId: String,
        @Body request: UpdateCollaboratorRequest
    ): Response<BoardCollaboratorResponse>
}
