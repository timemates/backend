package io.timemates.backend.authorization.repositories

import com.timemates.backend.time.UnixTime
import io.timemates.backend.authorization.types.Verification
import io.timemates.backend.authorization.types.value.Attempts
import io.timemates.backend.authorization.types.value.VerificationCode
import io.timemates.backend.authorization.types.value.VerificationHash
import io.timemates.backend.users.types.value.EmailAddress

/**
 * Repository that stores email verification codes.
 */
interface VerificationsRepository {
    suspend fun save(
        emailAddress: EmailAddress,
        verificationToken: VerificationHash,
        code: VerificationCode,
        time: UnixTime,
        attempts: Attempts,
    )

    suspend fun addAttempt(verificationToken: VerificationHash)
    suspend fun getVerification(verificationToken: VerificationHash): Verification?
    suspend fun remove(verificationToken: VerificationHash)

    /**
     * Gets count of all attempts happened after given time.
     * @param after what time we count.
     */
    suspend fun getNumberOfAttempts(
        emailAddress: EmailAddress, after: UnixTime,
    ): Int

    /**
     * Gets count of all sessions that was created after given time.
     */
    suspend fun getNumberOfSessions(
        emailAddress: EmailAddress,
        after: UnixTime,
    ): Int

    suspend fun markConfirmed(verificationToken: VerificationHash)
}