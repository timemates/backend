package io.timemates.backend.data.authorization.mapper

import com.timemates.backend.time.UnixTime
import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.authorization.types.Verification
import io.timemates.backend.authorization.types.value.Attempts
import io.timemates.backend.authorization.types.value.VerificationCode
import io.timemates.backend.data.authorization.db.entities.DbVerification
import io.timemates.backend.users.types.value.EmailAddress

class VerificationsMapper {
    fun dbToDomain(dbVerification: DbVerification): Verification {
        return Verification(
            emailAddress = EmailAddress.createOrThrow(dbVerification.emailAddress),
            code = VerificationCode.createOrThrow(dbVerification.code),
            attempts = Attempts.createOrThrow(dbVerification.attempts),
            time = UnixTime.createOrThrow(dbVerification.time),
            isConfirmed = dbVerification.isConfirmed
        )
    }
}