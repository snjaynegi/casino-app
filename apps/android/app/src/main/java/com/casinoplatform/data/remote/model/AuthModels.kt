package com.casinoplatform.data.remote.model

data class UserSummaryDto(
    val id: String,
    val email: String,
    val username: String,
    val role: String,
    val createdAt: String
)

data class AuthTokensDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

data class AuthResponseDto(
    val user: UserSummaryDto,
    val tokens: AuthTokensDto
)

data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String
)

data class LoginRequest(
    val emailOrUsername: String,
    val password: String
)

data class RefreshRequest(
    val refreshToken: String
)

data class PlayerMeResponse(
    val id: String,
    val email: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String?,
    val level: Int,
    val experiencePoints: Long,
    val balance: Long,
    val currencyCode: String,
    val dailyTimeLimitMinutes: Int?,
    val coolOffUntil: String?,
    val createdAt: String
)
