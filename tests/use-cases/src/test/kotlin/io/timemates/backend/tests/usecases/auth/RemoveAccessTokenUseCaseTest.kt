package io.timemates.backend.tests.usecases.auth

import io.timemates.backend.integrations.inmemory.repositories.InMemoryAuthorizationsRepository
import io.timemates.backend.repositories.AuthorizationsRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.usecases.auth.RemoveAccessTokenUseCase
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class RemoveAccessTokenUseCaseTest {
    private val authorizationsRepository = InMemoryAuthorizationsRepository()

    private val useCase = RemoveAccessTokenUseCase(authorizationsRepository)

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

        assert(useCase.invoke(accessToken) is RemoveAccessTokenUseCase.Result.Success)
        assert(
            authorizationsRepository.get(
                accessToken, UnixTime(0)
            ) == null
        )
    }

    @Test
    fun testNotFound(): Unit = runBlocking {
        assert(useCase.invoke(accessToken) is RemoveAccessTokenUseCase.Result.AuthorizationNotFound)
    }
}