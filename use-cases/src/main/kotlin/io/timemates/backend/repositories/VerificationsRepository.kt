package io.timemates.backend.repositories

import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.UnixTime

/**
 * Repository that stores email verification codes.
 */
interface VerificationsRepository {
    suspend fun save(
        emailAddress: EmailsRepository.EmailAddress,
        verificationToken: VerificationToken,
        code: Code,
        time: UnixTime,
        attempts: Count
    )
    suspend fun addAttempt(verificationToken: VerificationToken)
    suspend fun getVerification(verificationToken: VerificationToken): Verification?
    suspend fun remove(verificationToken: VerificationToken)

    /**
     * Gets count of all attempts happened after given time.
     * @param after what time we count.
     */
    suspend fun getNumberOfAttempts(after: UnixTime): Int

    /**
     * Gets count of all sessions that was created after given time.
     */
    suspend fun getNumberOfSessions(after: UnixTime): Int

    suspend fun markConfirmed(verificationToken: VerificationToken)

    class Verification(
        val emailAddress: EmailsRepository.EmailAddress,
        val code: Code,
        val attempts: Count,
        val time: UnixTime,
        val isConfirmed: Boolean
    )

    @JvmInline
    value class Code(val string: String) {
        init {
            require(string.length == SIZE)
        }

        companion object {
            const val SIZE = 5
        }
    }

    @JvmInline
    value class VerificationToken(val string: String) {
        companion object {
            const val SIZE = 128
        }
    }
}