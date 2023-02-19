package io.timemates.backend.tests.usecases.auth

import io.timemates.backend.integrations.inmemory.repositories.InMemoryEmailsRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryVerificationsRepository
import io.timemates.backend.providers.SecureRandomStringProvider
import io.timemates.backend.providers.SystemCurrentTimeProvider
import io.timemates.backend.repositories.EmailsRepository
import io.timemates.backend.usecases.auth.AuthByEmailUseCase
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.test.Test

class AuthByEmailUseCaseTest {
    private val verificationsRepository = InMemoryVerificationsRepository()
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())
    private val secureRandomStringProvider = SecureRandomStringProvider()
    private val emailsRepository = InMemoryEmailsRepository()

    private val useCase = AuthByEmailUseCase(
        emailsRepository, verificationsRepository, timeProvider, secureRandomStringProvider
    )

    private val email = EmailsRepository.EmailAddress("test@email.com")

    @Test
    fun testSuccess(): Unit = runBlocking {
        assert(useCase.invoke(email) is AuthByEmailUseCase.Result.Success)

        assert(emailsRepository.getEmails(email).any())
    }

    @Test
    fun testAttemptsExceed(): Unit = runBlocking {
        repeat(3) {
            useCase.invoke(email)
        }

        assert(useCase.invoke(email) is AuthByEmailUseCase.Result.AttemptsExceed)
    }
}