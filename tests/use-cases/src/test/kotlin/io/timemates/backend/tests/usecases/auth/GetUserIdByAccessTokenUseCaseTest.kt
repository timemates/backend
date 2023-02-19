package io.timemates.backend.tests.usecases.auth

import io.timemates.backend.providers.SystemCurrentTimeProvider
import kotlinx.coroutines.runBlocking
import io.timemates.backend.repositories.AuthorizationsRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryAuthorizationsRepository
import io.timemates.backend.usecases.auth.GetUserIdByAccessTokenUseCase
import java.time.Duration
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test

class GetUserIdByAccessTokenUseCaseTest {
    private val repository = InMemoryAuthorizationsRepository()
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())
    private val useCase = GetUserIdByAccessTokenUseCase(repository, timeProvider)

    @BeforeTest
    fun before() {
        runBlocking {
            repository.create(
                UsersRepository.UserId(0),
                AuthorizationsRepository.AccessToken("..."),
                AuthorizationsRepository.RefreshToken(".."),
                timeProvider.provide() + Duration.ofDays(7).toMillis()
            )
            repository.create(
                UsersRepository.UserId(1),
                AuthorizationsRepository.AccessToken("...."),
                AuthorizationsRepository.RefreshToken("....."),
                timeProvider.provide() + Duration.ofDays(7).toMillis()
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