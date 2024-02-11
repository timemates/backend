package io.timemates.backend.auth.domain.usecases

import com.timemates.backend.time.TimeProvider
import com.timemates.backend.time.UnixTime
import com.timemates.random.RandomProvider
import io.timemates.backend.auth.domain.repositories.EmailRepository
import io.timemates.backend.auth.domain.repositories.VerificationsRepository
import io.timemates.backend.types.auth.Email
import io.timemates.backend.types.auth.metadata.ClientMetadata
import io.timemates.backend.types.auth.value.Attempts
import io.timemates.backend.types.auth.value.VerificationCode
import io.timemates.backend.types.auth.value.VerificationHash
import io.timemates.backend.types.users.value.EmailAddress
import io.timemates.backend.validation.annotations.ValidationDelicateApi
import io.timemates.backend.validation.createUnsafe
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class AuthByEmailUseCase(
    private val emailRepository: EmailRepository,
    private val verifications: VerificationsRepository,
    private val timeProvider: TimeProvider,
    private val randomProvider: RandomProvider,
) {
    @OptIn(ValidationDelicateApi::class)
    suspend fun execute(emailAddress: EmailAddress, clientMetadata: ClientMetadata): Result {
        // used for limits (max count of sessions & attempts that can be requested)
        val sessionsTimeBoundary = timeProvider.provide() - 1.hours

        val attempts = verifications.getNumberOfAttempts(emailAddress, sessionsTimeBoundary)
            .getOrElse { throwable -> return Result.SendFailed(throwable) }
        val sessionsCount = verifications.getNumberOfSessions(emailAddress, sessionsTimeBoundary)
            .getOrElse { throwable -> return Result.SendFailed(throwable) }

        return if (attempts.int >= 9 || sessionsCount.int >= 3) {
            Result.AttemptsExceed
        } else {
            val code = VerificationCode.createUnsafe(randomProvider.randomHash(VerificationCode.SIZE))
            val verificationHash = VerificationHash.createUnsafe(randomProvider.randomHash(VerificationHash.SIZE))
            val expiresAt = timeProvider.provide() + 10.minutes
            val totalAttempts = Attempts.createUnsafe(3)

            if (!emailRepository.send(emailAddress, Email.AuthorizeEmail(emailAddress, code)))
                return Result.SendFailed(null)
            verifications.save(emailAddress, verificationHash, code, expiresAt, totalAttempts, clientMetadata)
            Result.Success(verificationHash, timeProvider.provide() + 10.minutes, totalAttempts)
        }
    }

    sealed interface Result {
        data class SendFailed(val throwable: Throwable?) : Result
        data class Success(
            val verificationHash: VerificationHash,
            val expiresAt: UnixTime,
            val attempts: Attempts,
        ) : Result

        data object AttemptsExceed : Result
    }
}