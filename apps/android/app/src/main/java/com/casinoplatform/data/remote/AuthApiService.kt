package com.casinoplatform.data.remote

import com.casinoplatform.data.remote.model.AuthResponseDto
import com.casinoplatform.data.remote.model.AuthTokensDto
import com.casinoplatform.data.remote.model.LoginRequest
import com.casinoplatform.data.remote.model.PlayerMeResponse
import com.casinoplatform.data.remote.model.RefreshRequest
import com.casinoplatform.data.remote.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponseDto>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponseDto>

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): Response<AuthTokensDto>

    @POST("auth/logout")
    suspend fun logout(@Body request: RefreshRequest): Response<Map<String, Boolean>>

    @GET("players/me")
    suspend fun getMe(@Header("Authorization") token: String): Response<PlayerMeResponse>

    @PATCH("players/me")
    suspend fun updateMe(
        @Header("Authorization") token: String,
        @Body updates: Map<String, Any>
    ): Response<PlayerMeResponse>

    @DELETE("players/me")
    suspend fun deleteMe(@Header("Authorization") token: String): Response<Map<String, Any>>
}
