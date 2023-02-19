package io.timemates.backend.integrations.inmemory.repositories

import io.timemates.backend.repositories.EmailsRepository
import io.timemates.backend.repositories.VerificationsRepository
import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.UnixTime
import java.util.concurrent.ConcurrentHashMap

class InMemoryVerificationsRepository : VerificationsRepository {
    private val map = ConcurrentHashMap<VerificationsRepository.VerificationToken, VerificationsRepository.Verification>()

    override suspend fun save(
        emailAddress: EmailsRepository.EmailAddress,
        verificationToken: VerificationsRepository.VerificationToken,
        code: VerificationsRepository.Code,
        time: UnixTime,
        attempts: Count
    ) {
        map[verificationToken] = VerificationsRepository.Verification(
            emailAddress, code, attempts, time, false
        )
    }

    override suspend fun addAttempt(verificationToken: VerificationsRepository.VerificationToken) {
        val old = map[verificationToken]!!
        map[verificationToken] = old.copy(attempts = Count(old.attempts.int - 1))
    }

    override suspend fun getVerification(verificationToken: VerificationsRepository.VerificationToken): VerificationsRepository.Verification? {
        return map[verificationToken]
    }

    override suspend fun remove(verificationToken: VerificationsRepository.VerificationToken) {
        map.remove(verificationToken)
    }

    override suspend fun getNumberOfAttempts(emailAddress: EmailsRepository.EmailAddress, after: UnixTime): Int {
        return map.filter {
            it.value.emailAddress == emailAddress && it.value.time.long > after.long
        }.asIterable().sumOf { it.value.attempts.int }
    }

    override suspend fun getNumberOfSessions(emailAddress: EmailsRepository.EmailAddress, after: UnixTime): Int {
        return map.filter {
            it.value.emailAddress == emailAddress && it.value.time.long > after.long
        }.size
    }

    override suspend fun markConfirmed(verificationToken: VerificationsRepository.VerificationToken) {
        map[verificationToken] = getVerification(verificationToken)!!.copy(isConfirmed = true)
    }
}