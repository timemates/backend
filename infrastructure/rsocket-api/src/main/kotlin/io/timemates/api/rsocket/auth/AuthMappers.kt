package io.timemates.api.rsocket.auth

import io.timemates.api.authorizations.types.Authorization
import io.timemates.api.authorizations.types.Metadata
import io.timemates.api.rsocket.internal.createOrFail
import io.timemates.backend.authorization.types.metadata.ClientMetadata
import io.timemates.backend.authorization.types.metadata.value.ClientIpAddress
import io.timemates.backend.authorization.types.metadata.value.ClientName
import io.timemates.backend.authorization.types.metadata.value.ClientVersion
import io.timemates.rsproto.server.RSocketService
import io.timemates.backend.authorization.types.Authorization as CoreAuthorization


context (RSocketService)
internal fun Metadata.core(): ClientMetadata {
    return ClientMetadata(
        clientName = ClientName.createOrFail(clientName),
        clientVersion = ClientVersion.createOrFail(clientVersion),
        clientIpAddress = ClientIpAddress.createOrFail("UNDEFINED"),
    )
}

internal fun CoreAuthorization.rs(): Authorization = Authorization {
    accessHash = Authorization.Hash {
        value = this@rs.accessHash.string
        expiresAt = this@rs.expiresAt.inMilliseconds
    }

    refreshHash = Authorization.Hash {
        value = this@rs.refreshAccessHash.string
        expiresAt = this@rs.expiresAt.inMilliseconds
    }
    generationTime = this@rs.createdAt.inMilliseconds
    metadata = this@rs.clientMetadata.rs()
    userId = this@rs.userId.long
}

internal fun ClientMetadata.rs(): Metadata = Metadata {
    clientName = this@rs.clientName.string
    clientVersion = this@rs.clientVersion.double
}