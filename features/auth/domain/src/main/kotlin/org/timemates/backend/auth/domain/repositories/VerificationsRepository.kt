package org.timemates.backend.auth.domain.repositories

import com.timemates.backend.time.UnixTime
import org.timemates.backend.types.auth.Verification
import org.timemates.backend.types.auth.metadata.ClientMetadata
import org.timemates.backend.types.auth.value.Attempts
import org.timemates.backend.types.auth.value.VerificationCode
import org.timemates.backend.types.auth.value.VerificationHash
import org.timemates.backend.types.common.value.Count
import org.timemates.backend.types.users.value.EmailAddress

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
    ): Result<Unit>

    /**
     * Adds an attempt to verify the email address associated with the specified [verificationToken].
     *
     * @param verificationToken The verification token used to confirm the email address.
     */
    suspend fun addAttempt(verificationToken: VerificationHash): Result<Unit>

    /**
     * Gets the verification record associated with the specified [verificationToken].
     *
     * @param verificationToken The verification token used to confirm the email address.
     *
     * @return The verification record associated with the specified [verificationToken], or `null` if not found.
     */
    suspend fun getVerification(verificationToken: VerificationHash): Result<Verification?>

    /**
     * Removes the verification record associated with the specified [verificationToken].
     *
     * @param verificationToken The verification token used to confirm the email address.
     */
    suspend fun remove(verificationToken: VerificationHash): Result<Unit>

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
    ): Result<Count>

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
    ): Result<Count>

    /**
     * Marks the verification associated with the specified [verificationToken] as confirmed.
     *
     * @param verificationToken The verification token used to confirm the email address.
     */
    suspend fun markConfirmed(verificationToken: VerificationHash)
}