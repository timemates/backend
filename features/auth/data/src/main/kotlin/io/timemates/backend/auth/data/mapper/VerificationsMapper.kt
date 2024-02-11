package io.timemates.backend.auth.data.mapper

import com.timemates.backend.time.UnixTime
import io.timemates.backend.auth.data.db.entities.DbVerification
import io.timemates.backend.types.auth.Verification
import io.timemates.backend.types.auth.metadata.ClientMetadata
import io.timemates.backend.types.auth.metadata.value.ClientIpAddress
import io.timemates.backend.types.auth.metadata.value.ClientName
import io.timemates.backend.types.auth.metadata.value.ClientVersion
import io.timemates.backend.types.auth.value.Attempts
import io.timemates.backend.types.auth.value.VerificationCode
import io.timemates.backend.types.users.value.EmailAddress
import io.timemates.backend.validation.annotations.ValidationDelicateApi
import io.timemates.backend.validation.createUnsafe

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