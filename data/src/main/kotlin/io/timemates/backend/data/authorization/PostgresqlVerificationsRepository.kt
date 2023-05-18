package io.timemates.backend.data.authorization

import com.timemates.backend.time.UnixTime
import io.timemates.backend.authorization.repositories.VerificationsRepository
import io.timemates.backend.authorization.types.Verification
import io.timemates.backend.authorization.types.value.Attempts
import io.timemates.backend.authorization.types.value.VerificationCode
import io.timemates.backend.authorization.types.value.VerificationHash
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.users.types.value.EmailAddress

class PostgresqlVerificationsRepository(

) : VerificationsRepository {
    override suspend fun save(emailAddress: EmailAddress, verificationToken: VerificationHash, code: VerificationCode, time: UnixTime, attempts: Attempts) {
        TODO("Not yet implemented")
    }

    override suspend fun addAttempt(verificationToken: VerificationHash) {
        TODO("Not yet implemented")
    }

    override suspend fun getVerification(verificationToken: VerificationHash): Verification? {
        TODO("Not yet implemented")
    }

    override suspend fun remove(verificationToken: VerificationHash) {
        TODO("Not yet implemented")
    }

    override suspend fun getNumberOfAttempts(emailAddress: EmailAddress, after: UnixTime): Count {
        TODO("Not yet implemented")
    }

    override suspend fun getNumberOfSessions(emailAddress: EmailAddress, after: UnixTime): Count {
        TODO("Not yet implemented")
    }

    override suspend fun markConfirmed(verificationToken: VerificationHash) {
        TODO("Not yet implemented")
    }
}