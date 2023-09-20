package io.timemates.backend.authorization.usecases

import com.timemates.backend.time.TimeProvider
import com.timemates.backend.time.UnixTime
import io.timemates.backend.validation.createOrThrowInternally
import com.timemates.random.RandomProvider
import io.timemates.backend.authorization.repositories.VerificationsRepository
import io.timemates.backend.authorization.types.Email
import io.timemates.backend.authorization.types.metadata.ClientMetadata
import io.timemates.backend.authorization.types.value.Attempts
import io.timemates.backend.authorization.types.value.VerificationCode
import io.timemates.backend.authorization.types.value.VerificationHash
import io.timemates.backend.common.markers.UseCase
import io.timemates.backend.common.repositories.EmailsRepository
import io.timemates.backend.users.types.value.EmailAddress
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class AuthByEmailUseCase(
    private val emails: EmailsRepository,
    private val verifications: VerificationsRepository,
    private val timeProvider: TimeProvider,
    private val randomProvider: RandomProvider,
) : UseCase {
    suspend fun execute(emailAddress: EmailAddress, clientMetadata: ClientMetadata): Result {
        // used for limits (max count of sessions & attempts that can be requested)
        val sessionsTimeBoundary = timeProvider.provide() - 1.hours

        return when {
            verifications.getNumberOfAttempts(emailAddress, sessionsTimeBoundary).int >= 9 ||
                verifications.getNumberOfSessions(emailAddress, sessionsTimeBoundary).int >= 3 ->
                Result.AttemptsExceed

            else -> {
                val code = VerificationCode.createOrThrowInternally(randomProvider.randomHash(VerificationCode.SIZE))
                val verificationHash = VerificationHash.createOrThrowInternally(randomProvider.randomHash(VerificationHash.SIZE))
                val expiresAt = timeProvider.provide() + 10.minutes
                val totalAttempts = Attempts.createOrThrowInternally(3)

                if (!emails.send(emailAddress, Email.AuthorizeEmail(emailAddress, code)))
                    return Result.SendFailed
                verifications.save(emailAddress, verificationHash, code, expiresAt, totalAttempts, clientMetadata)
                Result.Success(verificationHash, timeProvider.provide() + 10.minutes, totalAttempts)
            }
        }
    }

    sealed interface Result {
        data object SendFailed : Result
        data class Success(
            val verificationHash: VerificationHash,
            val expiresAt: UnixTime,
            val attempts: Attempts,
        ) : Result

        data object AttemptsExceed : Result
    }
}