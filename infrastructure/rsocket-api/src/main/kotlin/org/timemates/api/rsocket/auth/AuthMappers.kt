package org.timemates.api.rsocket.auth

import io.timemates.rsproto.server.RSocketService
import org.timemates.api.authorizations.types.Authorization
import org.timemates.api.authorizations.types.Metadata
import org.timemates.api.rsocket.internal.createOrFail
import org.timemates.backend.types.auth.metadata.ClientMetadata
import org.timemates.backend.types.auth.metadata.value.ClientIpAddress
import org.timemates.backend.types.auth.metadata.value.ClientName
import org.timemates.backend.types.auth.metadata.value.ClientVersion
import org.timemates.backend.types.auth.Authorization as CoreAuthorization


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