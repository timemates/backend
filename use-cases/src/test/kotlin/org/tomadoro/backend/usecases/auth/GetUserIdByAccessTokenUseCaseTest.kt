package org.tomadoro.backend.usecases.auth

import kotlinx.coroutines.runBlocking
import org.tomadoro.backend.providers.MockedCurrentTimeProvider
import org.tomadoro.backend.repositories.AuthorizationsRepository
import org.tomadoro.backend.repositories.MockedAuthorizationsRepository
import org.tomadoro.backend.repositories.UsersRepository
import java.time.Duration
import kotlin.test.BeforeTest
import kotlin.test.Test

class GetUserIdByAccessTokenUseCaseTest {
    private val repository = MockedAuthorizationsRepository()
    private val useCase = GetUserIdByAccessTokenUseCase(repository, MockedCurrentTimeProvider)

    @BeforeTest
    fun before() {
        runBlocking {
            repository.create(
                UsersRepository.UserId(0),
                AuthorizationsRepository.AccessToken("..."),
                AuthorizationsRepository.RefreshToken(".."),
                MockedCurrentTimeProvider.provide() + Duration.ofDays(7).toMillis()
            )
            repository.create(
                UsersRepository.UserId(1),
                AuthorizationsRepository.AccessToken("...."),
                AuthorizationsRepository.RefreshToken("....."),
                MockedCurrentTimeProvider.provide() + Duration.ofDays(7).toMillis()
            )
        }
    }

    @Test
    fun testSuccess(): Unit = runBlocking {
        val result =
            useCase(AuthorizationsRepository.AccessToken("..."))
        assert(result is GetUserIdByAccessTokenUseCase.Result.Success)
    }

    @Test
    fun testFailure() = runBlocking {
        val result =
            useCase(AuthorizationsRepository.AccessToken(".."))
        assert(result is GetUserIdByAccessTokenUseCase.Result.NotFound)
    }
}