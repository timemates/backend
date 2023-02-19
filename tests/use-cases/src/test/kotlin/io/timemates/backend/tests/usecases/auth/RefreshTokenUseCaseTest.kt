package io.timemates.backend.tests.usecases.auth

import io.timemates.backend.integrations.inmemory.repositories.InMemoryAuthorizationsRepository
import io.timemates.backend.providers.SecureRandomStringProvider
import io.timemates.backend.providers.SystemCurrentTimeProvider
import io.timemates.backend.repositories.AuthorizationsRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.usecases.auth.RefreshTokenUseCase
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.test.Test

class RefreshTokenUseCaseTest {
    private val authorizationsRepository = InMemoryAuthorizationsRepository()
    private val randomStringProvider = SecureRandomStringProvider()
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())

    private val useCase = RefreshTokenUseCase(
        randomStringProvider, authorizationsRepository, timeProvider
    )

    private val accessToken = AuthorizationsRepository.AccessToken("...")
    private val refreshToken = AuthorizationsRepository.RefreshToken("123")

    @Test
    fun testSuccess(): Unit = runBlocking {
        authorizationsRepository.create(
            UsersRepository.UserId(1),
            accessToken,
            refreshToken,
            UnixTime(123)
        )

        val result = useCase.invoke(refreshToken)

        assert(result is RefreshTokenUseCase.Result.Success)
        result as RefreshTokenUseCase.Result.Success

        assert(
            authorizationsRepository.get(
                accessToken, UnixTime(0)
            ) == null
        )

        assert(authorizationsRepository.get(result.accessToken, timeProvider.provide()) != null)
    }

    @Test
    fun testNotFound(): Unit = runBlocking {
        assert(
            useCase.invoke(
                AuthorizationsRepository.RefreshToken("...")
            ) is RefreshTokenUseCase.Result.InvalidAuthorization
        )
    }
}