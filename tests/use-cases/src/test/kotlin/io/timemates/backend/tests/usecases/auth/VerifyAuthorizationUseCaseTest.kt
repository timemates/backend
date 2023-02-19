package io.timemates.backend.tests.usecases.auth

import io.timemates.backend.integrations.inmemory.repositories.InMemoryAuthorizationsRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryUsersRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryVerificationsRepository
import io.timemates.backend.providers.SecureRandomStringProvider
import io.timemates.backend.providers.SystemCurrentTimeProvider
import io.timemates.backend.providers.provideVerificationCode
import io.timemates.backend.repositories.EmailsRepository
import io.timemates.backend.repositories.VerificationsRepository
import io.timemates.backend.types.value.Count
import io.timemates.backend.usecases.auth.VerifyAuthorizationUseCase
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.test.Test

class VerifyAuthorizationUseCaseTest {
    private val verificationsRepository = InMemoryVerificationsRepository()
    private val authorizationsRepository = InMemoryAuthorizationsRepository()
    private val usersRepository = InMemoryUsersRepository()
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())
    private val randomStringProvider = SecureRandomStringProvider()

    private val useCase = VerifyAuthorizationUseCase(
        verificationsRepository,
        authorizationsRepository,
        randomStringProvider,
        usersRepository,
        timeProvider
    )

    private val email = EmailsRepository.EmailAddress("email@example.com")
    private val code = VerificationsRepository.Code("12qwe")

    @Test
    fun testSuccess(): Unit = runBlocking {
        val token = VerificationsRepository.VerificationToken("12345")

        verificationsRepository.save(
            email,
            token,
            code,
            timeProvider.provide(),
            Count(3)
        )

        assert(useCase.invoke(token, code) is VerifyAuthorizationUseCase.Result.Success)
    }

    @Test
    fun `test attemptsExceed and attemptFailed`(): Unit = runBlocking {
        val token = VerificationsRepository.VerificationToken("abcde")

        verificationsRepository.save(
            email,
            token,
            code,
            timeProvider.provide(),
            Count(3)
        )

        repeat(3) {
            assert(
                useCase.invoke(
                    token,
                    randomStringProvider.provideVerificationCode()
                ) is VerifyAuthorizationUseCase.Result.AttemptFailed
            )
        }

        assert(
            useCase.invoke(
                token, randomStringProvider.provideVerificationCode()
            ) is VerifyAuthorizationUseCase.Result.AttemptsExceed
        )
    }
}