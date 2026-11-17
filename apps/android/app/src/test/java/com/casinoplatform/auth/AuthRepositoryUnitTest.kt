package com.casinoplatform.auth

import com.casinoplatform.data.repository.AuthRepository
import com.casinoplatform.data.repository.AuthState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthRepositoryUnitTest {

    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        authRepository = AuthRepository()
    }

    @Test
    fun initialState_isUnauthenticated() {
        assertEquals(AuthState.Unauthenticated, authRepository.authState.value)
    }

    @Test
    fun register_transitionsToAuthenticatedWithInitialVirtualCoins() = runBlocking {
        val result = authRepository.register("pilot@casino.test", "NeonPilot", "Password123!")

        assertTrue(result.isSuccess)
        val state = authRepository.authState.value
        assertTrue(state is AuthState.Authenticated)
        val authState = state as AuthState.Authenticated
        assertEquals("NeonPilot", authState.player.username)
        assertEquals(1000L, authState.player.balance)
        assertEquals("VCOIN", authState.player.currencyCode)
    }

    @Test
    fun logout_clearsAuthenticationState() = runBlocking {
        authRepository.register("pilot@casino.test", "NeonPilot", "Password123!")
        assertTrue(authRepository.authState.value is AuthState.Authenticated)

        authRepository.logout()
        assertEquals(AuthState.Unauthenticated, authRepository.authState.value)
    }
}
