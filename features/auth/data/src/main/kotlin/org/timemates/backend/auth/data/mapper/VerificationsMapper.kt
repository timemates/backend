package org.timemates.backend.auth.data.mapper

import com.timemates.backend.time.UnixTime
import org.timemates.backend.auth.data.db.entities.DbVerification
import org.timemates.backend.types.auth.Verification
import org.timemates.backend.types.auth.metadata.ClientMetadata
import org.timemates.backend.types.auth.metadata.value.ClientIpAddress
import org.timemates.backend.types.auth.metadata.value.ClientName
import org.timemates.backend.types.auth.metadata.value.ClientVersion
import org.timemates.backend.types.auth.value.Attempts
import org.timemates.backend.types.auth.value.VerificationCode
import org.timemates.backend.types.users.value.EmailAddress
import org.timemates.backend.validation.annotations.ValidationDelicateApi
import org.timemates.backend.validation.createUnsafe

@OptIn(ValidationDelicateApi::class)
class VerificationsMapper {
    fun dbToDomain(dbVerification: DbVerification): Verification {
        return Verification(
            emailAddress = EmailAddress.createUnsafe(dbVerification.emailAddress),
            code = VerificationCode.createUnsafe(dbVerification.code),
            attempts = Attempts.createUnsafe(dbVerification.attempts),
            time = UnixTime.createUnsafe(dbVerification.time),
            isConfirmed = dbVerification.isConfirmed,
            clientMetadata = ClientMetadata(
                clientName = ClientName.createUnsafe(dbVerification.metaClientName),
                clientVersion = ClientVersion.createUnsafe(dbVerification.metaClientVersion),
                clientIpAddress = ClientIpAddress.createUnsafe(dbVerification.metaClientIpAddress),
            )
        )
    }
}