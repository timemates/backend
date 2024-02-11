package io.timemates.backend.auth.data

import com.timemates.backend.time.UnixTime
import io.timemates.backend.auth.data.db.TableVerificationsDataSource
import io.timemates.backend.auth.data.mapper.VerificationsMapper
import io.timemates.backend.auth.domain.repositories.VerificationsRepository
import io.timemates.backend.types.auth.Verification
import io.timemates.backend.types.auth.metadata.ClientMetadata
import io.timemates.backend.types.auth.value.Attempts
import io.timemates.backend.types.auth.value.VerificationCode
import io.timemates.backend.types.auth.value.VerificationHash
import io.timemates.backend.types.common.value.Count
import io.timemates.backend.types.users.value.EmailAddress
import io.timemates.backend.validation.annotations.ValidationDelicateApi
import io.timemates.backend.validation.createUnsafe

class PostgresqlVerificationsRepository(
    private val dbVerifications: TableVerificationsDataSource,
    private val mapper: VerificationsMapper,
) : VerificationsRepository {
    override suspend fun save(
        emailAddress: EmailAddress,
        verificationToken: VerificationHash,
        code: VerificationCode,
        time: UnixTime,
        attempts: Attempts,
        clientMetadata: ClientMetadata,
    ): Result<Unit> = runCatching {
        dbVerifications.add(
            emailAddress.string,
            verificationToken.string,
            code.string,
            time.inMilliseconds,
            attempts.int,
            clientMetadata.clientName.string,
            clientMetadata.clientVersion.double,
            clientMetadata.clientIpAddress.string
        )
    }

    override suspend fun addAttempt(verificationToken: VerificationHash): Result<Unit> = runCatching {
        dbVerifications.decreaseAttempts(verificationToken.string)
    }

    override suspend fun getVerification(verificationToken: VerificationHash): Result<Verification?> = runCatching {
        dbVerifications.getVerification(verificationToken.string)?.let(mapper::dbToDomain)
    }

    override suspend fun remove(verificationToken: VerificationHash): Result<Unit> = runCatching {
        dbVerifications.remove(verificationToken.string)
    }

    @OptIn(ValidationDelicateApi::class)
    override suspend fun getNumberOfAttempts(emailAddress: EmailAddress, after: UnixTime): Result<Count> = runCatching {
        dbVerifications.getAttempts(emailAddress.string, after.inMilliseconds)
            .let { Count.createUnsafe(it) }
    }

    @OptIn(ValidationDelicateApi::class)
    override suspend fun getNumberOfSessions(emailAddress: EmailAddress, after: UnixTime): Result<Count> = runCatching {
        dbVerifications.getSessionsCount(emailAddress.string, after.inMilliseconds)
            .let { Count.createUnsafe(it) }
    }

    override suspend fun markConfirmed(verificationToken: VerificationHash) {
        dbVerifications.setAsConfirmed(verificationToken.string)
    }
}