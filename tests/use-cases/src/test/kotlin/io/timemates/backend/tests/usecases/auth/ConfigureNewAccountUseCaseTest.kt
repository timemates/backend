package io.timemates.backend.tests.usecases.auth

import io.timemates.backend.integrations.inmemory.repositories.InMemoryAuthorizationsRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryUsersRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryVerificationsRepository
import io.timemates.backend.providers.SecureRandomStringProvider
import io.timemates.backend.providers.SystemCurrentTimeProvider
import io.timemates.backend.repositories.EmailsRepository
import io.timemates.backend.repositories.VerificationsRepository
import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.UserName
import io.timemates.backend.usecases.auth.ConfigureNewAccountUseCase
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.test.Test

class ConfigureNewAccountUseCaseTest {
    private val verificationsRepository = InMemoryVerificationsRepository()
    private val authorizationsRepository = InMemoryAuthorizationsRepository()
    private val usersRepository = InMemoryUsersRepository()
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())
    private val randomStringProvider = SecureRandomStringProvider()

    private val useCase = ConfigureNewAccountUseCase(
        usersRepository,
        authorizationsRepository,
        verificationsRepository,
        timeProvider,
        randomStringProvider
    )

    private val email = EmailsRepository.EmailAddress("email@example.com")
    private val token = VerificationsRepository.VerificationToken("12345")
    private val code = VerificationsRepository.Code("12qwe")

    @Test
    fun testSuccess(): Unit = runBlocking {
        verificationsRepository.save(
            email,
            token,
            code,
            timeProvider.provide(),
            Count(3)
        )

        verificationsRepository.markConfirmed(token)

        assert(
            useCase.invoke(
                token, UserName("test"), null
            ) is ConfigureNewAccountUseCase.Result.Success
        )
    }

    @Test
    fun testNotFound(): Unit = runBlocking {
        verificationsRepository.save(
            email,
            token,
            code,
            timeProvider.provide(),
            Count(3)
        )

        // operation should fail as verification wasn't confirmed
        assert(
            useCase.invoke(
                token, UserName("test"), null
            ) is ConfigureNewAccountUseCase.Result.NotFound
        )

        // should fail as there is no such token
        assert(
            useCase.invoke(
                VerificationsRepository.VerificationToken("12345"), UserName("test"), null
            ) is ConfigureNewAccountUseCase.Result.NotFound
        )
    }
}