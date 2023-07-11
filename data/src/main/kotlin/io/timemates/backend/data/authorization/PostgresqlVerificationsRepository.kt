package io.timemates.backend.data.authorization

import com.timemates.backend.time.UnixTime
import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.authorization.repositories.VerificationsRepository
import io.timemates.backend.authorization.types.Verification
import io.timemates.backend.authorization.types.metadata.ClientMetadata
import io.timemates.backend.authorization.types.value.Attempts
import io.timemates.backend.authorization.types.value.VerificationCode
import io.timemates.backend.authorization.types.value.VerificationHash
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.data.authorization.db.TableVerificationsDataSource
import io.timemates.backend.data.authorization.mapper.VerificationsMapper
import io.timemates.backend.users.types.value.EmailAddress

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
    ) {
        dbVerifications.add(
            emailAddress.string,
            verificationToken.string,
            code.string,
            time.inMilliseconds,
            attempts.int,
            clientMetadata.clientName.string,
            clientMetadata.clientVersion.string,
            clientMetadata.clientIpAddress.string
        )
    }

    override suspend fun addAttempt(verificationToken: VerificationHash) {
        dbVerifications.decreaseAttempts(verificationToken.string)
    }

    override suspend fun getVerification(verificationToken: VerificationHash): Verification? {
        return dbVerifications.getVerification(verificationToken.string)?.let(mapper::dbToDomain)
    }

    override suspend fun remove(verificationToken: VerificationHash) {
        dbVerifications.remove(verificationToken.string)
    }

    override suspend fun getNumberOfAttempts(emailAddress: EmailAddress, after: UnixTime): Count {
        return dbVerifications.getAttempts(emailAddress.string, after.inMilliseconds)
            .let { Count.createOrThrow(it) }
    }

    override suspend fun getNumberOfSessions(emailAddress: EmailAddress, after: UnixTime): Count {
        return dbVerifications.getSessionsCount(emailAddress.string, after.inMilliseconds)
            .let { Count.createOrThrow(it) }
    }

    override suspend fun markConfirmed(verificationToken: VerificationHash) {
        dbVerifications.setAsConfirmed(verificationToken.string)
    }
}