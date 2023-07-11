package io.timemates.backend.authorization.repositories

import com.timemates.backend.time.UnixTime
import io.timemates.backend.authorization.types.Verification
import io.timemates.backend.authorization.types.metadata.ClientMetadata
import io.timemates.backend.authorization.types.value.Attempts
import io.timemates.backend.authorization.types.value.VerificationCode
import io.timemates.backend.authorization.types.value.VerificationHash
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.users.types.value.EmailAddress

/**
 * Repository that stores email verification codes.
 */
interface VerificationsRepository {

    /**
     * Saves a new email verification record with the specified [emailAddress], [verificationToken], [code],
     * [time], and [attempts].
     *
     * @param emailAddress The email address associated with the verification record.
     * @param verificationToken The verification token used to confirm the email address.
     * @param code The verification code sent to the email address.
     * @param time The time at which the verification record was created.
     * @param attempts The number of attempts made to verify the email address.
     */
    suspend fun save(
        emailAddress: EmailAddress,
        verificationToken: VerificationHash,
        code: VerificationCode,
        time: UnixTime,
        attempts: Attempts,
        clientMetadata: ClientMetadata,
    )

    /**
     * Adds an attempt to verify the email address associated with the specified [verificationToken].
     *
     * @param verificationToken The verification token used to confirm the email address.
     */
    suspend fun addAttempt(verificationToken: VerificationHash)

    /**
     * Gets the verification record associated with the specified [verificationToken].
     *
     * @param verificationToken The verification token used to confirm the email address.
     *
     * @return The verification record associated with the specified [verificationToken], or `null` if not found.
     */
    suspend fun getVerification(verificationToken: VerificationHash): Verification?

    /**
     * Removes the verification record associated with the specified [verificationToken].
     *
     * @param verificationToken The verification token used to confirm the email address.
     */
    suspend fun remove(verificationToken: VerificationHash)

    /**
     * Gets the number of verification attempts made for the email address associated with the specified
     * [emailAddress], after the specified [after] time.
     *
     * @param emailAddress The email address associated with the verification record.
     * @param after The time after which the verification attempts were made.
     *
     * @return The number of verification attempts made for the email address associated with the specified
     * [emailAddress], after the specified [after] time.
     */
    suspend fun getNumberOfAttempts(
        emailAddress: EmailAddress, after: UnixTime,
    ): Count

    /**
     * Gets the number of verification sessions created for the email address associated with the specified
     * [emailAddress], after the specified [after] time.
     *
     * @param emailAddress The email address associated with the verification record.
     * @param after The time after which the verification sessions were created.
     *
     * @return The number of verification sessions created for the email address associated with the specified
     * [emailAddress], after the specified [after] time.
     */
    suspend fun getNumberOfSessions(
        emailAddress: EmailAddress,
        after: UnixTime,
    ): Count

    /**
     * Marks the verification associated with the specified [verificationToken] as confirmed.
     *
     * @param verificationToken The verification token used to confirm the email address.
     */
    suspend fun markConfirmed(verificationToken: VerificationHash)
}