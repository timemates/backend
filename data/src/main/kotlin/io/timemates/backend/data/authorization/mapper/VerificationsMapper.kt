package io.timemates.backend.data.authorization.mapper

import com.timemates.backend.time.UnixTime
import io.timemates.backend.validation.createOrThrowInternally
import io.timemates.backend.authorization.types.Verification
import io.timemates.backend.authorization.types.metadata.ClientMetadata
import io.timemates.backend.authorization.types.metadata.value.ClientIpAddress
import io.timemates.backend.authorization.types.metadata.value.ClientName
import io.timemates.backend.authorization.types.metadata.value.ClientVersion
import io.timemates.backend.authorization.types.value.Attempts
import io.timemates.backend.authorization.types.value.VerificationCode
import io.timemates.backend.data.authorization.db.entities.DbVerification
import io.timemates.backend.data.common.markers.Mapper
import io.timemates.backend.users.types.value.EmailAddress

class VerificationsMapper : Mapper {
    fun dbToDomain(dbVerification: DbVerification): Verification {
        return Verification(
            emailAddress = EmailAddress.createOrThrowInternally(dbVerification.emailAddress),
            code = VerificationCode.createOrThrowInternally(dbVerification.code),
            attempts = Attempts.createOrThrowInternally(dbVerification.attempts),
            time = UnixTime.createOrThrowInternally(dbVerification.time),
            isConfirmed = dbVerification.isConfirmed,
            clientMetadata = ClientMetadata(
                clientName = ClientName.createOrThrowInternally(dbVerification.metaClientName),
                clientVersion = ClientVersion.createOrThrowInternally(dbVerification.metaClientVersion),
                clientIpAddress = ClientIpAddress.createOrThrowInternally(dbVerification.metaClientIpAddress),
            )
        )
    }
}