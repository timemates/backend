package io.timemates.backend.usecases.auth

import io.timemates.backend.providers.CurrentTimeProvider
import io.timemates.backend.providers.RandomStringProvider
import io.timemates.backend.providers.provideVerificationCode
import io.timemates.backend.providers.provideVerificationToken
import io.timemates.backend.repositories.EmailsRepository
import io.timemates.backend.repositories.VerificationsRepository
import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.UnixTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class AuthByEmailUseCase(
    private val emails: EmailsRepository,
    private val verifications: VerificationsRepository,
    private val timeProvider: CurrentTimeProvider,
    private val randomStringProvider: RandomStringProvider
) {
    suspend operator fun invoke(emailAddress: EmailsRepository.EmailAddress): Result {
        // used for limits (max count of sessions & attempts that can be requested)
        val sessionsTimeBoundary = timeProvider.provide() - 1.hours

        return when {
            verifications.getNumberOfAttempts(sessionsTimeBoundary) > 9 ||
                verifications.getNumberOfSessions(sessionsTimeBoundary) > 3 ->
                Result.AttemptsExceed
            else -> {
                val code = randomStringProvider.provideVerificationCode()
                val verificationToken = randomStringProvider.provideVerificationToken()
                val expiresAt = timeProvider.provide() + 10.minutes
                val totalAttempts = Count(3)

                emails.send(emailAddress, EmailsRepository.Subject("TimeMates Confirmation"), generateEmail(code))
                verifications.save(emailAddress, verificationToken, code, expiresAt, totalAttempts)
                Result.Success(timeProvider.provide() + 10.minutes, totalAttempts)
            }
        }
    }

    private fun generateEmail(
        code: VerificationsRepository.Code
    ) = EmailsRepository.MessageBody(
        """
            Your authorization code for TimeMates is ${code.string}.
            
            If you haven't requested for authorization, just ignore this letter.
        """.trimIndent()
    )

    sealed interface Result {
        object SendFailed : Result
        class Success(
            val expiresAt: UnixTime,
            val attempts: Count
        ) : Result

        object AttemptsExceed : Result
    }
}