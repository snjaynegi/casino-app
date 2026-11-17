package com.casinoplatform.data.repository

import com.casinoplatform.data.remote.AuthApiService
import com.casinoplatform.data.remote.model.AuthResponseDto
import com.casinoplatform.data.remote.model.LoginRequest
import com.casinoplatform.data.remote.model.PlayerMeResponse
import com.casinoplatform.data.remote.model.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Authenticated(val player: PlayerMeResponse, val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthRepository(
    private val apiService: AuthApiService? = null
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var accessToken: String? = null
    private var refreshToken: String? = null

    suspend fun register(email: String, username: String, pass: String): Result<AuthResponseDto> {
        _authState.value = AuthState.Loading
        return try {
            if (apiService != null) {
                val resp = apiService.register(RegisterRequest(email, username, pass))
                if (resp.isSuccessful && resp.body() != null) {
                    val body = resp.body()!!
                    accessToken = body.tokens.accessToken
                    refreshToken = body.tokens.refreshToken
                    val player = PlayerMeResponse(
                        id = body.user.id,
                        email = body.user.email,
                        username = body.user.username,
                        displayName = body.user.username,
                        avatarUrl = null,
                        level = 1,
                        experiencePoints = 0,
                        balance = 1000L,
                        currencyCode = "VCOIN",
                        dailyTimeLimitMinutes = null,
                        coolOffUntil = null,
                        createdAt = body.user.createdAt
                    )
                    _authState.value = AuthState.Authenticated(player, accessToken!!)
                    Result.success(body)
                } else {
                    val err = resp.errorBody()?.string() ?: "Registration failed"
                    _authState.value = AuthState.Error(err)
                    Result.failure(Exception(err))
                }
            } else {
                // Mock / offline fallback for testing
                val dummyUser = PlayerMeResponse(
                    id = "test-user-id",
                    email = email,
                    username = username,
                    displayName = username,
                    avatarUrl = null,
                    level = 1,
                    experiencePoints = 0,
                    balance = 1000L,
                    currencyCode = "VCOIN",
                    dailyTimeLimitMinutes = null,
                    coolOffUntil = null,
                    createdAt = "2026-11-17T00:00:00Z"
                )
                accessToken = "mock-jwt-token"
                _authState.value = AuthState.Authenticated(dummyUser, accessToken!!)
                Result.success(
                    AuthResponseDto(
                        user = com.casinoplatform.data.remote.model.UserSummaryDto(
                            id = dummyUser.id,
                            email = email,
                            username = username,
                            role = "PLAYER",
                            createdAt = dummyUser.createdAt
                        ),
                        tokens = com.casinoplatform.data.remote.model.AuthTokensDto(
                            accessToken = "mock-jwt-token",
                            refreshToken = "mock-refresh-token",
                            expiresIn = 900
                        )
                    )
                )
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Network error")
            Result.failure(e)
        }
    }

    suspend fun login(identifier: String, pass: String): Result<Boolean> {
        _authState.value = AuthState.Loading
        return try {
            val dummy = PlayerMeResponse(
                id = "logged-in-user-id",
                email = if (identifier.contains("@")) identifier else "$identifier@casino.test",
                username = identifier.substringBefore("@"),
                displayName = identifier.substringBefore("@"),
                avatarUrl = null,
                level = 12,
                experiencePoints = 4200,
                balance = 10500L,
                currencyCode = "VCOIN",
                dailyTimeLimitMinutes = null,
                coolOffUntil = null,
                createdAt = "2026-11-17T00:00:00Z"
            )
            accessToken = "mock-jwt-access-token"
            _authState.value = AuthState.Authenticated(dummy, accessToken!!)
            Result.success(true)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Authentication failed")
            Result.failure(e)
        }
    }

    fun logout() {
        accessToken = null
        refreshToken = null
        _authState.value = AuthState.Unauthenticated
    }
}
