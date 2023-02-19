package io.timemates.backend.application.repositories

import io.timemates.backend.integrations.postgresql.repositories.datasource.DbVerificationsDataSource
import io.timemates.backend.repositories.EmailsRepository
import io.timemates.backend.repositories.VerificationsRepository
import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.UnixTime

class DbVerificationsRepository(
    private val dbVerificationsDataSource: DbVerificationsDataSource
) : VerificationsRepository {
    override suspend fun save(
        emailAddress: EmailsRepository.EmailAddress,
        verificationToken: VerificationsRepository.VerificationToken,
        code: VerificationsRepository.Code,
        time: UnixTime,
        attempts: Count
    ) {
        dbVerificationsDataSource.add(
            emailAddress.string,
            verificationToken.string,
            code.string,
            time.long,
            attempts.int
        )
    }

    override suspend fun addAttempt(verificationToken: VerificationsRepository.VerificationToken) {
        dbVerificationsDataSource.addAttempt(verificationToken.string)
    }

    override suspend fun getVerification(verificationToken: VerificationsRepository.VerificationToken): VerificationsRepository.Verification? {
        return dbVerificationsDataSource.getVerification(verificationToken.string)
            ?.toExternal()
    }

    override suspend fun remove(verificationToken: VerificationsRepository.VerificationToken) {
        dbVerificationsDataSource.remove(verificationToken.string)
    }

    override suspend fun getNumberOfAttempts(emailAddress: EmailsRepository.EmailAddress, after: UnixTime): Int {
        return dbVerificationsDataSource.getAttemptsCount(
            emailAddress.string, after.long
        )
    }

    override suspend fun getNumberOfSessions(emailAddress: EmailsRepository.EmailAddress, after: UnixTime): Int {
        return dbVerificationsDataSource.getCountOfConfirmation(
            emailAddress.string, after.long
        ).toInt()
    }

    override suspend fun markConfirmed(verificationToken: VerificationsRepository.VerificationToken) {
        dbVerificationsDataSource.setConfirmed(verificationToken.string)
    }

    private fun DbVerificationsDataSource.Verification.toExternal() =
        VerificationsRepository.Verification(
            EmailsRepository.EmailAddress(emailAddress),
            VerificationsRepository.Code(code),
            Count(attempts),
            UnixTime(time),
            isConfirmed
        )
}